package com.lightningkite.mirror

import com.lightningkite.lokalize.Date
import com.lightningkite.lokalize.Month
import com.lightningkite.lokalize.Year
import com.lightningkite.recktangle.Point
import com.lightningkite.mirror.info.Type
import com.lightningkite.mirror.info.type
import kotlin.test.Test
import com.lightningkite.mirror.serialization.json.JsonSerializer
import kotlin.test.assertEquals

class Tests {

    fun <T : Any> test(value: T, type: Type<T>) {
        val result = JsonSerializer.write(value, type)
        println(result)
        val back = JsonSerializer.read(result, type)
    }

    init {
        configureMirror()
    }

    @Test
    fun testSerializeDate() {
        val stringSerializer = JsonSerializer.encoder(String::class.type)
        JsonSerializer.addEncoder(Date::class.type) {
            stringSerializer.invoke(this, it.iso8601())
        }
        assertEquals("\"1993-08-25\"", JsonSerializer.write(Date(Year(1993), Month.August, 25), Date::class.type))
    }

    @Test
    fun testPoint() {
        val point = Point(4f, 8f)
        test(point, Point::class.type)
    }
}