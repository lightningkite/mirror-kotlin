package com.lightningkite.mirror.serialization.json

import com.lightningkite.lokalize.*
import com.lightningkite.lokalize.time.*
import com.lightningkite.mirror.TestRegistry
import com.lightningkite.mirror.info.*
import com.lightningkite.recktangle.Point
import com.lightningkite.mirror.serialization.DefaultRegistry
import kotlin.test.Test
import com.lightningkite.mirror.serialization.json.JsonSerializer
import com.lightningkite.recktangle.PointClassInfo
import kotlin.reflect.KClass
import kotlin.test.assertEquals

class ReflectionSerializerTest {
    
    val serializer = JsonSerializer(DefaultRegistry + TestRegistry)

    fun <T : Any> test(value: T, type: Type<T>) {
        val result = serializer.write(value, type)
        println(result)
        val back = serializer.read(result, type)
    }

    @Test
    fun testPoint() {
        val point = Point(4f, 8f)
        test(point, Point::class.type)
    }

    @Test
    fun testTimeStamp() {
        val point = TimeStamp.now()
        test(point, TimeStamp::class.type)
    }

    @Test
    fun testSerializeDate() {
        val stringSerializer = serializer.encoder(String::class.type)
        serializer.addEncoder(Date::class.type) {
            stringSerializer.invoke(this, it.iso8601())
        }
        assertEquals("\"1993-08-25\"", serializer.write(Date(Year(1993), Month.August, 25), Date::class.type))
    }
}