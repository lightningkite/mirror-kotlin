package com.lightningkite.mirror.metadata

import com.lightningkite.mirror.*
import me.eugeniomarletti.kotlin.metadata.classKind
import me.eugeniomarletti.kotlin.metadata.declaresDefaultValue
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.builtins.BuiltInSerializerProtocol
import me.eugeniomarletti.kotlin.metadata.shadow.builtins.BuiltInsBinaryVersion
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.returnType
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.type
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.upperBounds
import java.io.InputStream


fun InputStream.readPackageFragment() = PackageFragmentReader(use { stream ->
    val version = BuiltInsBinaryVersion.readFrom(stream)

    if (!version.isCompatible()) {
        throw UnsupportedOperationException(
                "Kotlin built-in definition format version is not supported: " +
                        "expected ${BuiltInsBinaryVersion.INSTANCE}, actual $version. " +
                        "Please update Kotlin"
        )
    }

    ProtoBuf.PackageFragment.parseFrom(stream, BuiltInSerializerProtocol.extensionRegistry)
}
)

class PackageFragmentReader(val fragment: ProtoBuf.PackageFragment) {

    fun hasFullName(name: String) = fragment.qualifiedNames.qualifiedNameList.any {
        it?.resolve() == name
    }

    fun Int.resolve(): String = fragment.strings.getString(this)
    fun resolveString(has: Boolean, pointer: Int) = if (has) fragment.strings.getString(pointer) else null
    fun resolveQualifiedName(has: Boolean, pointer: Int) = if (has) fragment.qualifiedNames.getQualifiedName(pointer) else null
    fun ProtoBuf.QualifiedNameTable.QualifiedNameOrBuilder.resolve(): String {
        val strings = ArrayList<String>()
        var current: ProtoBuf.QualifiedNameTable.QualifiedNameOrBuilder? = this
        while (current != null) {
            strings.add(current.shortName.resolve())
            current = resolveQualifiedName(current.hasParentQualifiedName(), current.parentQualifiedName)
        }
        return strings.asReversed().joinToString(".")
    }


    fun read(): List<ReadClassInfo> {
        return this.fragment.class_List.flatMap {
            it.classInfos()
        }
    }

    fun ProtoBuf.Class.classInfos(): List<ReadClassInfo> {
        val fullName = resolveQualifiedName(hasFqName(), fqName)?.resolve() ?: return listOf()
        val packageName = fullName.split('.').takeWhile {
            it.firstOrNull()?.isLowerCase() ?: false
        }.joinToString(".")
        val name = fullName.substringAfterLast('.')

        val table = TypeTable(typeTable)

        val info = ReadClassInfo(
                imports = listOf(),
                modifiers = when (this.classKind) {
                    ProtoBuf.Class.Kind.CLASS -> listOf()
                    ProtoBuf.Class.Kind.INTERFACE -> listOf(ReadClassInfo.Modifier.Interface)
                    ProtoBuf.Class.Kind.ENUM_CLASS -> listOf()
                    ProtoBuf.Class.Kind.ENUM_ENTRY -> listOf()
                    ProtoBuf.Class.Kind.ANNOTATION_CLASS -> listOf()
                    ProtoBuf.Class.Kind.OBJECT -> listOf()
                    ProtoBuf.Class.Kind.COMPANION_OBJECT -> listOf()
                } + when (modality) {
                    ProtoBuf.Modality.FINAL -> listOf()
                    ProtoBuf.Modality.OPEN -> listOf(ReadClassInfo.Modifier.Open)
                    ProtoBuf.Modality.ABSTRACT -> listOf(ReadClassInfo.Modifier.Abstract)
                    ProtoBuf.Modality.SEALED -> listOf(ReadClassInfo.Modifier.Sealed)
                    null -> listOf()
                } + if (isDataClass) {
                    listOf(ReadClassInfo.Modifier.Data)
                } else listOf(),
                implements = this.supertypeList.map { it.read(table, listOf(this)) },
                packageName = packageName,
                owner = fullName.removePrefix(packageName).removeSuffix(name).trim('.').takeUnless { it.isBlank() },
                name = name,
                typeParameters = typeParameterList.map { it.read(table, listOf(this)) },
                enumValues = if (classKind == ProtoBuf.Class.Kind.ENUM_CLASS)
                    enumEntryList.mapNotNull {
                        resolveString(it.hasName(), it.name)
                    }
                else null,
                annotations = listOf(), //hasAnnotations
                fields = readFields(this.constructorList.firstOrNull(), this.propertyList, table)
        )
        return listOf(info)
    }

    fun ProtoBuf.Class.readFields(constructor: ProtoBuf.Constructor?, properties: List<ProtoBuf.Property>, table: TypeTable): List<ReadSerializedFieldInfo> {
        val propertyNames = properties.associate { resolveString(it.hasName(), it.name) to it.returnType(table).read(table, listOf(this)) }
        if (constructor == null) return emptyList()
        val props = ArrayList<ReadSerializedFieldInfo>()
        for (argument in constructor.valueParameterList) {
            val argName = resolveString(argument.hasName(), argument.name) ?: continue
            val argType = argument.type(table).read(table, listOf(this))
            val correspondingPropertyType = propertyNames[argName] ?: continue
            if (correspondingPropertyType == argType) {
                //Wahoo!  We found one!
                props.add(ReadSerializedFieldInfo(
                        name = argName,
                        type = argType,
                        isOptional = argument.declaresDefaultValue,
                        default = null
                ))
            }
        }
        return props
    }

    fun ProtoBuf.Type.read(typeTable: TypeTable, containingClasses: List<ProtoBuf.Class>): ReadType {
        return ReadType(
                kClass = resolveString(hasTypeParameterName(), typeParameterName)
                        ?: (if (hasTypeParameter())
                            containingClasses
                                    .flatMap { it.typeParameterList }
                                    .find { typeParameter == it.id  }
                                    ?.name?.let { resolveString(true, it) }
                        else null)
                        ?: resolveQualifiedName(hasClassName(), className)?.resolve()
                        ?: "Any",
                typeArguments = argumentList.map {
                    it.read(typeTable, containingClasses)
                },
                isNullable = this.nullable
        )
    }

    fun ProtoBuf.Type.Argument.read(typeTable: TypeTable, containingClasses: List<ProtoBuf.Class>): ReadTypeProjection {
        val type = this.type(typeTable)?.read(typeTable, containingClasses)
        return ReadTypeProjection(
                //Panic A only occurs here
                type = type ?: ReadType("Any", isNullable = true),
                variance = when (this.projection) {
                    ProtoBuf.Type.Argument.Projection.IN -> ReadTypeProjection.Variance.IN
                    ProtoBuf.Type.Argument.Projection.OUT -> ReadTypeProjection.Variance.OUT
                    ProtoBuf.Type.Argument.Projection.INV -> ReadTypeProjection.Variance.INVARIANT
                    ProtoBuf.Type.Argument.Projection.STAR -> ReadTypeProjection.Variance.STAR
                    else -> ReadTypeProjection.Variance.INVARIANT
                }
        )
    }

    fun ProtoBuf.TypeParameter.read(typeTable: TypeTable, containingClasses: List<ProtoBuf.Class>): ReadTypeParameter {
        return ReadTypeParameter(
                name = resolveString(hasName(), name)!!,
                projection = ReadTypeProjection(
                        type = this.upperBounds(typeTable).firstOrNull()?.read(typeTable, containingClasses)
                                ?: ReadType("Any", isNullable = true),
                        variance = when (variance) {
                            ProtoBuf.TypeParameter.Variance.IN -> ReadTypeProjection.Variance.IN
                            ProtoBuf.TypeParameter.Variance.OUT -> ReadTypeProjection.Variance.OUT
                            ProtoBuf.TypeParameter.Variance.INV -> ReadTypeProjection.Variance.INVARIANT
                            else -> ReadTypeProjection.Variance.INVARIANT
                        }
                )
        )
    }
}