//package com.lightningkite.mirror.old
//
//import com.lightningkite.mirror.recursiveFlatMap
//
//interface NavigableDeclaration{
//    fun subs():Sequence<NavigableDeclaration>
//}
//
//fun NavigableDeclaration.recursiveSubs() = subs().recursiveFlatMap { it.subs() }
//
//class KxvDirectory(val qualifiedValName: String, val classes: List<String>)
//
//data class ReadFileInfo(
//        var fileName: String,
//        var packageName: String,
//        var generalImports: List<String>,
//        var specificImports: List<String>,
//        var classes: List<ReadClassInfo>
//): NavigableDeclaration {
//    override fun subs(): Sequence<NavigableDeclaration>
//        = classes.asSequence()
//}
//
//data class ReadClassInfo(
//        var simpleName: String,
//        var packageName: String,
//        var implements: List<ReadType>,
//        var typeParameters: List<ReadTypeParameter>,
//        var fields: Map<String, ReadField> = mapOf(),
//        var annotations: List<ReadAnnotationInfo> = listOf(),
//        var modifiers: List<ClassInfoModifier> = listOf(),
//        var enumValues: List<String>? = null
//): NavigableDeclaration {
//
//    enum class Modifier {
//        Sealed,
//        Abstract,
//        Data,
//        Open,
//        Interface
//    }
//
//    val qualifiedName get() = "$packageName.$simpleName"
//    val reflectiveObjectName get() = simpleName.filter { it.isJavaIdentifierPart() } + "ClassInfo"
//    val qualifiedReflectiveObjectName get() = "$packageName.$reflectiveObjectName"
//    var selfType = ReadType(simpleName, false, (0 until typeParameters.size).map { ReadTypeProjection.STAR })
//    var selfTypeAny = ReadType(simpleName, false, typeParameters.map { ReadTypeProjection(it.minimum) })
//
//    override fun subs(): Sequence<NavigableDeclaration>
//            = implements.asSequence() +
//            fields.values.asSequence() +
////            typeParameters.asSequence() +
//            annotations.asSequence()
//}
//
//data class ReadTypeParameter(
//        var name: String,
//        var minimum: ReadType = ReadType("Any", true),
//        var variance: KxVariance = KxVariance.INVARIANT
//): NavigableDeclaration {
//    override fun subs(): Sequence<NavigableDeclaration>
//            = sequenceOf(minimum)
//}
//
//data class ReadTypeProjection(
//        var type: ReadType,
//        var variance: KxVariance = KxVariance.INVARIANT,
//        var isStar: Boolean = false
//): NavigableDeclaration {
//    companion object {
//        var STAR = ReadTypeProjection(ReadType("Any", true), isStar = true)
//    }
//
//    fun deepCopy(): ReadTypeProjection = copy(type = type.deepCopy())
//
//    override fun subs(): Sequence<NavigableDeclaration>
//            = sequenceOf(type)
//}
//
//
//data class ReadType(
//        var base: String,
//        var nullable: Boolean = false,
//        var typeParameters: List<ReadTypeProjection> = listOf(),
//        var annotations: List<ReadAnnotationInfo> = listOf()
//): NavigableDeclaration {
//    override fun subs(): Sequence<NavigableDeclaration>
//            = typeParameters.asSequence() + annotations.asSequence()
//    fun deepCopy() = copy(typeParameters = typeParameters.map { it.deepCopy() })
//}
//
//data class ReadField(
//        var name: String,
//        var type: ReadType,
//        var annotations: List<ReadAnnotationInfo>,
//        var isOptional: Boolean = false
//        var default: String?
//): NavigableDeclaration {
//    override fun subs(): Sequence<NavigableDeclaration>
//            = annotations.asSequence() + sequenceOf(type)
//}
//
//data class ReadAnnotationInfo(
//        var name: String,
//        var arguments: List<String>,
//        var useSiteTarget: String? = null
//): NavigableDeclaration {
//    override fun subs(): Sequence<NavigableDeclaration> = sequenceOf()
//}