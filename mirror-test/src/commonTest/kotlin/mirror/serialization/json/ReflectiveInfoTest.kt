package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.TestRegistry
import com.lightningkite.mirror.info.*
import com.lightningkite.mirror.serialization.DefaultRegistry
import com.lightningkite.recktangle.PointClassInfo
import kotlin.reflect.KClass
import kotlin.test.Test
import com.lightningkite.recktangle.Point

class ReflectiveInfoTest {

    val serializer = JsonSerializer(DefaultRegistry + TestRegistry)

    fun <T : Any> test(value: T, type: Type<T>) {
        val result = serializer.write(value, type)
        println(result)
        val back = serializer.read(result, type)
    }


    @Test
    fun testRoundTripFieldInfo() {
        val type = FieldInfo::class.type
        test(PointClassInfo.fieldX, type)
        test(PointClassInfo.fieldY, type)
    }

    @Test
    fun testRoundTripFieldInfoSpecific() {
        val type = Type<FieldInfo<Point, *>>(FieldInfo::class, listOf(TypeProjection(Point::class.type)))
        test(PointClassInfo.fieldX, type)
        test(PointClassInfo.fieldY, type)
    }

    @Test
    fun testRoundTripKClass() {
        val type = KClass::class.type
        test(Any::class, type)
        test(Unit::class, type)
        test(Boolean::class, type)
        test(Byte::class, type)
        test(Short::class, type)
        test(Int::class, type)
        test(Long::class, type)
        test(Float::class, type)
        test(Double::class, type)
        test(Number::class, type)
        test(Char::class, type)
        test(String::class, type)
        test(List::class, type)
        test(Map::class, type)
        test(Point::class, type)
    }

    @Test
    fun testRoundTripClassInfo() {
        val type = ClassInfo::class.type
        test(serializer.registry.classInfoRegistry[Any::class]!!, type)
        test(serializer.registry.classInfoRegistry[Unit::class]!!, type)
        test(serializer.registry.classInfoRegistry[Boolean::class]!!, type)
        test(serializer.registry.classInfoRegistry[Byte::class]!!, type)
        test(serializer.registry.classInfoRegistry[Short::class]!!, type)
        test(serializer.registry.classInfoRegistry[Int::class]!!, type)
        test(serializer.registry.classInfoRegistry[Long::class]!!, type)
        test(serializer.registry.classInfoRegistry[Float::class]!!, type)
        test(serializer.registry.classInfoRegistry[Double::class]!!, type)
        test(serializer.registry.classInfoRegistry[Number::class]!!, type)
        test(serializer.registry.classInfoRegistry[Char::class]!!, type)
        test(serializer.registry.classInfoRegistry[String::class]!!, type)
        test(serializer.registry.classInfoRegistry[List::class]!!, type)
        test(serializer.registry.classInfoRegistry[Map::class]!!, type)
        test(serializer.registry.classInfoRegistry[Point::class]!!, type)
    }
}
