//package com.lightningkite.mirror.source
//
//import com.lightningkite.mirror.old.*
//
//fun Node.getFile(fileName: String): ReadFileInfo {
//    val packageName = this["packageHeader"]?.get("identifier")?.toStringIdentifier() ?: ""
//    val classes = this.children
//            .filter { it.type == "topLevelObject" }
//            .mapNotNull { it["classDeclaration"]?.toKxClasses(packageName) }
//            .flatMap { it }
//    val generalImports = this["importList"]?.children?.mapNotNull {
//        if (it.terminals.contains("*")) {
//            it["identifier"]!!.children.joinToString(".") { it.content!! }
//        } else null
//    } ?: listOf()
//    val specificImports = this["importList"]?.children?.mapNotNull {
//        if (!it.terminals.contains("*")) {
//            it["identifier"]!!.children.joinToString(".") { it.content!! }
//        } else null
//    } ?: listOf()
//    return ReadFileInfo(
//            fileName = fileName,
//            packageName = packageName,
//            specificImports = specificImports,
//            generalImports = generalImports,
//            classes = classes
//    )
//}
//
//fun Node.toStringIdentifier(): String = children.mapNotNull {
//    when(it.type){
//        "simpleIdentifier" -> it.content
//        "simpleUserType" -> it.toStringIdentifier()
//        else -> null
//    }
//}.joinToString(".")
//
//fun Node.toKxClasses(packageName: String, owner: ReadClassInfo? = null): List<ReadClassInfo> {
//    val directName = this["simpleIdentifier"]!!.content!!
//    val simpleName = owner?.let{
//        it.simpleName + "." + directName
//    } ?: directName
//    val constructorVarList = this["primaryConstructor"]
//            ?.get("classParameters")
//            ?.children?.filter { it.type == "classParameter" }
//            ?.mapNotNull { it.toKxConstructorVariable() } ?: listOf()
//    val normalVarList = (this["classBody"] ?: this["enumClassBody"])?.children
//            ?.filter { it.type == "classMemberDeclaration" }
//            ?.mapNotNull { it["propertyDeclaration"] }
//            ?.map { it.toKxVariable() } ?: listOf()
//    val typeParams = get("typeParameters")?.children
//            ?.filter { it.type == "typeParameter" }
//            ?.map {
//                ReadTypeParameter(
//                        name = it["simpleIdentifier"]!!.content!!,
//                        minimum = it["type"]?.toKxType() ?: ReadType("Any", true)
//                )
//            }
//            ?: listOf()
//    val implementsList = get("delegationSpecifiers")?.children?.mapNotNull {
//        it.get("constructorInvocation")?.toKxType()
//    } ?: listOf()
//    val modifiers = (this["modifierList"]?.children?.filter { it.type == "modifier" }?.mapNotNull { KxClassModifierMap[it.content] }
//            ?: listOf()) + (
//            if (this.terminals.contains("interface"))
//                listOf(ClassInfoModifier.Interface)
//            else
//                listOf()
//            )
//    val enumValues = this["enumClassBody"]?.get("enumEntries")?.children?.mapNotNull { it["simpleIdentifier"]?.content }
//    val canBeInstantiated = !(modifiers.contains(ClassInfoModifier.Interface) ||
//            modifiers.contains(ClassInfoModifier.Abstract) ||
//            modifiers.contains(ClassInfoModifier.Sealed) ||
//            enumValues != null)
//    val constructors = listOfNotNull(this["primaryConstructor"]?.toKxConstructor(simpleName, typeParams) ?: if(canBeInstantiated) KxvFunction(
//            name = simpleName,
//            type = ReadType(simpleName, false, typeParams.map { ReadTypeProjection.STAR }, listOf()),
//            typeParameters = typeParams.map { it.copy() },
//            arguments = listOf(),
//            annotations = listOf()
//    ) else null)
//    val currentClass = ReadClassInfo(
//            simpleName = simpleName,
//            packageName = packageName,
//            implements = implementsList,
//            typeParameters = typeParams,
//            fields = (constructorVarList + normalVarList).associate { it.name to it },
//            functions = listOf(),
//            constructors = constructors,
//            annotations = kxAnnotationsFromModifierList("class"),
//            modifiers = modifiers,
//            enumValues = enumValues
//    )
//    val subclasses:List<ReadClassInfo> = this["classBody"]?.children
//            ?.filter { it.type == "classMemberDeclaration" }
//            ?.mapNotNull { it["classDeclaration"]?.toKxClasses(packageName, currentClass) }
//            ?.flatMap { it }
//            ?: listOf()
//
//    return subclasses + currentClass
//}
//
//fun Node.kxAnnotationsFromModifierList(targeting: String): List<ReadAnnotationInfo> {
//    return get("modifierList")?.getAll("annotations")?.flatMap { it.children }?.map { it.toKxAnnotation() }?.filter { it.useSiteTarget == null || it.useSiteTarget == targeting }
//            ?: listOf()
//}
//
//fun Node.toKxAnnotation(): ReadAnnotationInfo {
//    return ReadAnnotationInfo(
//            name = this["unescapedAnnotation"]?.get("identifier")?.toStringIdentifier() ?: terminals[0].drop(1),
//            arguments = this["valueArguments"]?.children?.mapNotNull {
//                it.children.lastOrNull { it.type == "expression" }?.content
//            } ?: listOf(),
//            useSiteTarget = this["annotationUseSiteTarget"]?.terminals?.firstOrNull()
//    )
//}
//
//val anyNullableType = ReadType("Any", true, listOf(), listOf())
//fun Node.toKxType(annotations: List<ReadAnnotationInfo> = listOf()): ReadType {
//    return when (type) {
//        "nullableType" -> this.children.firstOrNull()?.toKxType()?.copy(nullable = true)
//                ?: anyNullableType
//        "type", "typeProjection", "typeReference" -> this.children.firstOrNull()?.toKxType()
//                ?: anyNullableType
//        "userType" -> {
//            if (children.count { it.type == "simpleUserType" } == 1) get("simpleUserType")!!.toKxType()
//            else ReadType(
//                    base = toStringIdentifier(),
//                    typeParameters = children.lastOrNull()
//                            ?.get("typeArguments")
//                            ?.children
//                            ?.map { it.toKxTypeProjection() }
//                            ?: listOf(),
//                    nullable = false
//            )
//        }
//        "simpleUserType" -> ReadType(
//                base = toStringIdentifier(),
//                nullable = false,
//                typeParameters = this["typeArguments"]?.children?.map { it.toKxTypeProjection() }
//                        ?: listOf(),
//                annotations = annotations
//        )
//        "constructorInvocation" -> ReadType(
//                base = this["userType"]?.toKxType()?.base ?: return anyNullableType,
//                nullable = false,
//                typeParameters = this["callSuffixLambdaless"]?.get("typeArguments")?.children?.map { it.toKxTypeProjection() }
//                        ?: listOf(),
//                annotations = annotations
//        )
//        else -> anyNullableType
//    }
//}
//
//fun Node.toKxTypeProjection(annotations: List<ReadAnnotationInfo> = listOf()): ReadTypeProjection {
//    return ReadTypeProjection(
//            type = this["type"]?.toKxType(annotations)
//                    ?: anyNullableType,
//            variance = this["typeProjectionModifierList"]?.get("varianceAnnotation")?.terminals?.firstOrNull()?.let { KxVariance.valueOf(it.toUpperCase()) }
//                    ?: KxVariance.INVARIANT,
//            isStar = this["type"] == null
//    )
//}
//
//fun Node.toKxConstructor(forName: String, typeParams: List<ReadTypeParameter>): KxvFunction {
//    val args = this["classParameters"]!!.children.map { it.toKxConstructorParam() }
//    return KxvFunction(
//            name = forName,
//            type = ReadType(forName, false, typeParams.map { ReadTypeProjection.STAR }, listOf()),
//            typeParameters = typeParams.map { it.copy() },
//            arguments = args,
//            annotations = listOf()
//    )
//}
//
//fun Node.toKxConstructorParam(): KxvArgument = KxvArgument(
//        name = this["simpleIdentifier"]!!.content!!,
//        type = this["type"]!!.toKxType(),
//        annotations = listOf(),
//        default = this.children.lastOrNull { it.type == "expression" }?.content
//)
//
//fun Node.toKxConstructorVariable(): ReadField? {
//    if (!terminals.contains("var") && !terminals.contains("val")) return null
//    return ReadField(
//            name = this["simpleIdentifier"]!!.content!!,
//            type = this["type"]!!.toKxType(),
//            annotations = kxAnnotationsFromModifierList("property"),
//            artificial = false,
//            mutable = terminals.contains("var")
//    )
//}
//
//fun Node.toKxVariable(): ReadField {
//    val varDec = this["variableDeclaration"]!!
//    return ReadField(
//            name = varDec["simpleIdentifier"]!!.content!!,
//            type = varDec["type"]?.toKxType()
//                    ?: ReadType("Any", true, listOf(), listOf()),
//            mutable = this.terminals.contains("var"),
//            artificial = this["getter"] != null,
//            annotations = kxAnnotationsFromModifierList("property")
//    )
//}
