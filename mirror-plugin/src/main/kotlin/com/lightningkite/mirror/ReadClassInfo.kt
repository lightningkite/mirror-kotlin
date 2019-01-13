package com.lightningkite.mirror

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.File

class ReadClassInfo(
        val imports: List<String> = listOf(),
        val modifiers: List<ReadClassInfo.Modifier> = listOf(),
        val implements: List<ReadType> = listOf(),
        val packageName: String = "",
        val owner: String? = null,
        val name: String = "Type",
        val typeParameters: List<ReadTypeParameter> = listOf(),
        val enumValues: List<String>? = null,
        val annotations: List<AnnotationInfo> = listOf(),
        val fields: List<ReadFieldInfo> = listOf(),
        val hasCompanion: Boolean = false
) {

    var fromFile: File? = null

    companion object {
        const val GENERATED_NOTICE = "AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT"
        val defaults:Map<String, String> = mapOf(
                "Unit" to "Unit",
                "Boolean" to "false",
                "Char" to "' '",
                "String" to "\"\"",
                "UByte" to "0.toUnsignedByte()",
                "UShort" to "0.toUnsignedShort()",
                "UInt" to "0U",
                "ULong" to "0UL",
                "Byte" to "0.toByte()",
                "Short" to "0.toShort()",
                "Int" to "0",
                "Long" to "0L",
                "Float" to "0f",
                "Double" to "0.0",
                "List" to "listOf()",
                "Map" to "mapOf()"
        )
    }

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface,
        Inline,
        Object;

        companion object {
            val map = values().associate { it.name.toLowerCase() to it }
        }
    }

    val reflectionPackage: String @JsonIgnore get(){
        return if(packageName.startsWith("kotlin")){
            "mirror.$packageName"
        } else packageName
    }
    val reflectionQualifiedName: String @JsonIgnore get(){
        return "$reflectionPackage.$reflectionName"
    }
    val reflectionName: String @JsonIgnore get() = "${owner ?: ""}${name}ClassInfo"
    val accessName: String @JsonIgnore get() = (if (owner == null) "" else owner + ".") + name

    val accessNameWithStars: String
        @JsonIgnore get() = (accessName + if (typeParameters.isNotEmpty())
            typeParameters.joinToString(",", "<", ">") { "*" }
        else
            "")
    val accessNameWithBound: String
        @JsonIgnore get() = ReadType(
                accessName,
                typeParameters.map { ReadTypeProjection(ReadType(it.name), ReadTypeProjection.Variance.INVARIANT) },
                false
        ).useMinimumBound(this)

    @JsonIgnore val requiredFields = fields.filter { !it.isOptional }
    @JsonIgnore val optionalFields = fields.filter { it.isOptional }
    val qualifiedName: String
        @JsonIgnore get() = "$packageName.$accessName"

    fun generateConstructor(): String {
        if (modifiers.contains(Modifier.Interface) || modifiers.contains(Modifier.Abstract) || modifiers.contains(Modifier.Sealed) || enumValues != null) {
            return """
                    |    override fun construct(map: Map<String, Any?>): $accessNameWithBound = throw NotImplementedError()
                    """.trimMargin()
        }
        if (modifiers.contains(Modifier.Object)) {
            return """
                    |    override fun construct(map: Map<String, Any?>): $accessNameWithBound = $accessName
                    """.trimMargin()
        }
        return """
            |    override fun construct(map: Map<String, Any?>): $accessNameWithBound {
            |        //Gather variables
            |        ${
        requiredFields
                .joinToString("\n        ") {
                    "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as ${it.type.useMinimumBound(this)}"
                }
            }
            |        //Handle the optionals
            |        ${
        optionalFields
                .withIndex()
                .joinToString("\n        ") { (index, it) ->
                    if (it.default != null) {
                        //We have a default calculation?  Awesome!
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as? ${it.type.useMinimumBound(this)} ?: ${it.default}"
                    } else if (it.type.nullable) {
                        //Oh good, let's just use null if it's not there
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as ${it.type.useMinimumBound(this)}"
                    } else {
                        //Well... I guess we'll retrieve it by calling the constructor an extra time.
                        val args = requiredFields.asSequence().map { it.name + " = " + it.name } + optionalFields.subList(0, index).asSequence().map { it.name + " = " + it.name }
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as? ${it.type.useMinimumBound(this)} ?: (${it.fieldName}.get($accessNameWithBound(${args.joinToString()})) as ${it.type.useMinimumBound(this)})"
                    }
                }
        }
            |        //Finally do the call
            |        return $accessNameWithBound(
            |            ${fields.joinToString(",\n            ") { it.name + " = " + it.name }}
            |        )
            |    }
            """.trimMargin()
    }

    fun filePart(): String = """
        |@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST", "USELESS_CAST")
        |object $reflectionName: ClassInfo<$accessNameWithStars> {
        |
        |    override val kClass: KClass<$accessNameWithStars> = $accessName::class
        |    override val modifiers: List<ClassInfo.Modifier> = listOf(${modifiers.joinToString { "ClassInfo.Modifier." + it.name }})
        |    override val companion: Any? get() = ${if(hasCompanion) accessName else "null"}
        |
        |    override val implements: List<Type<*>> = ${implements.joinToString(", ", "listOf(", ")"){it.toString(this)}}
        |
        |    override val packageName: String = "$packageName"
        |    override val owner: KClass<*>? = ${if (owner == null) null else "$owner::class"}
        |    override val ownerName: String? = ${if (owner == null) null else "\"$owner\""}
        |
        |    override val name: String = "$name"
        |    override val annotations: List<AnnotationInfo> = listOf(${annotations.joinToString()})
        |    override val enumValues: List<$accessNameWithStars>? = ${if (enumValues == null) "null" else enumValues.joinToString(", ", "listOf(", ")"){ "$accessName.$it" }}
        |
        |    ${
    fields.joinToString("\n    ") { it.toString(this) }
    }
        |
        |    override val fields:List<FieldInfo<$accessNameWithStars, *>> = listOf(${fields.joinToString { it.fieldName }})
        |
        |${generateConstructor()}
        |
        |}
        |
    """.trimMargin()

    @JsonIgnore val fullImports = (imports + "com.lightningkite.mirror.info.*" + "kotlin.reflect.KClass").distinct()

    override fun toString(): String = """
        |//Generated by Lightning Kite's Mirror plugin
        |//$GENERATED_NOTICE
        |package $reflectionPackage
        |
        |${fullImports.joinToString("\n"){ "import $it" }}
        |
        |${filePart()}""".trimMargin()
}
