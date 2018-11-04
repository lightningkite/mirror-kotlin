package com.lightningkite.mirror.source

import com.lightningkite.mirror.*

fun Node.getFileClasses(): List<ReadClassInfo> {
    val packageName = this["packageHeader"]?.get("identifier")?.toStringIdentifier() ?: ""
    val imports = this["importList"]?.children?.map {
        if (it.terminals.contains("*")) {
            it["identifier"]!!.children.joinToString(".") { it.content!! } + ".*"
        } else it["identifier"]!!.children.joinToString(".") { it.content!! }
    } ?: listOf()
    return this.children
            .filter { it.type == "topLevelObject" }
            .mapNotNull { it["classDeclaration"]?.toKxClasses(packageName, imports) }
            .flatMap { it }
}

fun Node.toStringIdentifier(): String = children.mapNotNull {
    when (it.type) {
        "simpleIdentifier" -> it.content
        "simpleUserType" -> it.toStringIdentifier()
        else -> null
    }
}.joinToString(".")

fun Node.toKxClasses(packageName: String, imports: List<String>, owner: ReadClassInfo? = null): List<ReadClassInfo> {
    val directName = this["simpleIdentifier"]!!.content!!
    val constructorVarList = this["primaryConstructor"]
            ?.get("classParameters")
            ?.children?.filter { it.type == "classParameter" }
            ?.mapNotNull { it.toKxConstructorVariable() } ?: listOf()
    val typeParams = get("typeParameters")?.children
            ?.filter { it.type == "typeParameter" }
            ?.map {
                ReadTypeParameter(
                        name = it["simpleIdentifier"]!!.content!!,
                        projection = ReadTypeProjection(it["type"]?.toKxType()
                                ?: ReadType("Any", isNullable = true), ReadTypeProjection.Variance.EXACT)
                )
            }
            ?: listOf()
    val implementsList = get("delegationSpecifiers")?.children?.mapNotNull {
        it.get("constructorInvocation")?.toKxType()
    } ?: listOf()
    val modifiers = (this["modifierList"]?.children?.filter { it.type == "modifier" }?.mapNotNull { ReadClassInfo.Modifier.map[it.content] }
            ?: listOf()) + (
            if (this.terminals.contains("interface"))
                listOf(ReadClassInfo.Modifier.Interface)
            else
                listOf()
            )
    val enumValues = this["enumClassBody"]?.get("enumEntries")?.children?.mapNotNull { it["simpleIdentifier"]?.content }
    val currentClass = ReadClassInfo(
            imports = imports,
            packageName = packageName,
            name = directName,
            owner = owner?.name,
            implements = implementsList,
            typeParameters = typeParams,
            fields = constructorVarList,
            annotations = kxAnnotationsFromModifierList("class"),
            modifiers = modifiers,
            enumValues = enumValues
    )
    val subclasses: List<ReadClassInfo> = this["classBody"]?.children
            ?.filter { it.type == "classMemberDeclaration" }
            ?.mapNotNull { it["classDeclaration"]?.toKxClasses(packageName, imports, currentClass) }
            ?.flatMap { it }
            ?: listOf()

    return subclasses + currentClass
}

fun Node.kxAnnotationsFromModifierList(targeting: String): List<AnnotationInfo> {
    return get("modifierList")?.getAll("annotations")?.flatMap { it.children }?.map { it.toKxAnnotation() }?.filter { it.useSiteTarget == null || it.useSiteTarget == targeting }
            ?: listOf()
}

fun Node.toKxAnnotation(): AnnotationInfo {
    return AnnotationInfo(
            name = this["unescapedAnnotation"]?.get("identifier")?.toStringIdentifier() ?: terminals[0].drop(1),
            arguments = this["valueArguments"]?.children?.mapNotNull {
                it.children.lastOrNull { it.type == "expression" }?.content
            } ?: listOf(),
            useSiteTarget = this["annotationUseSiteTarget"]?.terminals?.firstOrNull()
    )
}

val anyNullableType = ReadType("Any", isNullable = true)
fun Node.toKxType(): ReadType {
    return when (type) {
        "constructorInvocation" -> ReadType(
                kClass = this["userType"]?.toKxType()?.kClass ?: return anyNullableType,
                isNullable = false,
                typeArguments = this["callSuffixLambdaless"]?.get("typeArguments")?.children?.map { it.toKxTypeProjection() }
                        ?: listOf()
        )
        "nullableType" -> this.children.firstOrNull()?.toKxType()?.copy(isNullable = true)
                ?: anyNullableType
        "type", "typeProjection", "typeReference" -> this.children.firstOrNull()?.toKxType()
                ?: anyNullableType
        "userType" -> {
            if (children.count { it.type == "simpleUserType" } == 1) get("simpleUserType")!!.toKxType()
            else ReadType(
                    kClass = toStringIdentifier(),
                    typeArguments = children.lastOrNull()
                            ?.get("typeArguments")
                            ?.children
                            ?.map { it.toKxTypeProjection() }
                            ?: listOf(),
                    isNullable = false
            )
        }
        "simpleUserType" -> ReadType(
                kClass = toStringIdentifier(),
                isNullable = false,
                typeArguments = this["typeArguments"]?.children?.map { it.toKxTypeProjection() }
                        ?: listOf()
        )
        else -> anyNullableType
    }
}

fun Node.toKxTypeProjection(): ReadTypeProjection {
    if (this["type"] == null) {
        return ReadTypeProjection(ReadType("Any", isNullable = true), ReadTypeProjection.Variance.STAR)
    }
    return ReadTypeProjection(
            type = this["type"]?.toKxType()
                    ?: anyNullableType,
            variance = this["typeProjectionModifierList"]?.get("varianceAnnotation")?.terminals?.firstOrNull()?.let { ReadTypeProjection.Variance.valueOf(it.toUpperCase()) }
                    ?: ReadTypeProjection.Variance.EXACT
    )
}

fun Node.toKxConstructorVariable(): ReadSerializedFieldInfo? {
    if (!terminals.contains("var") && !terminals.contains("val")) return null
    return ReadSerializedFieldInfo(
            name = this["simpleIdentifier"]!!.content!!,
            type = this["type"]!!.toKxType(),
            annotations = kxAnnotationsFromModifierList("property"),
            default = this.children.lastOrNull { it.type == "expression" }?.content
    )
}