package com.lightningkite.mirror.info

object AnyClassInfo : EmptyClassInfo<Any>(Any::class, "kotlin", "Any") {
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf(ClassInfo.Modifier.Abstract)
}

object UnitClassInfo : EmptyClassInfo<Unit>(Unit::class, "kotlin", "Unit")
object BooleanClassInfo : EmptyClassInfo<Boolean>(Boolean::class, "kotlin", "Boolean") {
    override val enumValues: List<Boolean> get() = listOf(false, true)
}

object ByteClassInfo : EmptyClassInfo<Byte>(Byte::class, "kotlin", "Byte")
object ShortClassInfo : EmptyClassInfo<Short>(Short::class, "kotlin", "Short")
object IntClassInfo : EmptyClassInfo<Int>(Int::class, "kotlin", "Int")
object LongClassInfo : EmptyClassInfo<Long>(Long::class, "kotlin", "Long")
object FloatClassInfo : EmptyClassInfo<Float>(Float::class, "kotlin", "Float")
object DoubleClassInfo : EmptyClassInfo<Double>(Double::class, "kotlin", "Double")
object NumberClassInfo : EmptyClassInfo<Number>(Number::class, "kotlin", "Number") {
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf(ClassInfo.Modifier.Abstract)
}

object CharClassInfo : EmptyClassInfo<Char>(Char::class, "kotlin", "Char")
object StringClassInfo : EmptyClassInfo<String>(String::class, "kotlin", "String")
object ListClassInfo : EmptyClassInfo<List<*>>(List::class, "kotlin", "List")
object MapClassInfo : EmptyClassInfo<Map<*, *>>(Map::class, "kotlin", "Map")
