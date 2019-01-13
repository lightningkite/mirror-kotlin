package com.lightningkite.mirror.info

import com.lightningkite.kommon.native.SharedImmutable

@SharedImmutable
val PrimitiveClassInfoRegistry : ClassInfoRegistry = ClassInfoRegistry(
        AnyClassInfo,
        UnitClassInfo,
        BooleanClassInfo,
        ByteClassInfo,
        ShortClassInfo,
        IntClassInfo,
        LongClassInfo,
        FloatClassInfo,
        DoubleClassInfo,
        NumberClassInfo,
        CharClassInfo,
        StringClassInfo,
        RegexClassInfo,
        ListClassInfo,
        MapClassInfo,
        ComparableClassInfo
)

object AnyClassInfo : EmptyClassInfo<Any>(Any::class, 0, "kotlin", "Any") {
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf(ClassInfo.Modifier.Abstract)
}

object UnitClassInfo : EmptyClassInfo<Unit>(Unit::class, Unit, "kotlin", "Unit")
object BooleanClassInfo : EmptyClassInfo<Boolean>(Boolean::class, false, "kotlin", "Boolean") {
    override val enumValues: List<Boolean> get() = listOf(false, true)
}

object ByteClassInfo : EmptyClassInfo<Byte>(Byte::class, 0, "kotlin", "Byte")
object ShortClassInfo : EmptyClassInfo<Short>(Short::class, 0, "kotlin", "Short")
object IntClassInfo : EmptyClassInfo<Int>(Int::class, 0, "kotlin", "Int")
object LongClassInfo : EmptyClassInfo<Long>(Long::class, 0L, "kotlin", "Long")
object FloatClassInfo : EmptyClassInfo<Float>(Float::class, 0f, "kotlin", "Float")
object DoubleClassInfo : EmptyClassInfo<Double>(Double::class, 0.0, "kotlin", "Double")
object NumberClassInfo : EmptyClassInfo<Number>(Number::class, 0.0, "kotlin", "Number") {
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf(ClassInfo.Modifier.Abstract)
}

object CharClassInfo : EmptyClassInfo<Char>(Char::class, ' ', "kotlin", "Char")
object StringClassInfo : EmptyClassInfo<String>(String::class, "", "kotlin", "String")
object RegexClassInfo : EmptyClassInfo<Regex>(Regex::class, Regex(""), "kotlin", "Regex")
object ListClassInfo : EmptyClassInfo<List<*>>(List::class, listOf<Any?>(), "kotlin", "List")
object MapClassInfo : EmptyClassInfo<Map<*, *>>(Map::class, mapOf<Any?, Any?>(), "kotlin", "Map")

object ComparableClassInfo : EmptyClassInfo<Comparable<*>>(Comparable::class, 0, "kotlin", "Comparable"){
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf(ClassInfo.Modifier.Abstract)
}