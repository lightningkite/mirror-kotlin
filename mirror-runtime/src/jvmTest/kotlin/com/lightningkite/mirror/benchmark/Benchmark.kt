package com.lightningkite.mirror.benchmark

import com.lightningkite.mirror.TestRegistry
import com.lightningkite.mirror.info.type
import com.lightningkite.mirror.serialization.DefaultRegistry
import com.lightningkite.mirror.serialization.json.JsonSerializer
import com.lightningkite.mirror.test.Attitude
import com.lightningkite.mirror.test.IntData
import com.lightningkite.mirror.test.Tree
import com.lightningkite.mirror.test.Zoo
import kotlinx.serialization.json.Json
import kotlin.test.Test

class Benchmark {

    val instance = Zoo(
            Unit, true, 10, 20, 30, 40, 50f, 60.0, 'A', "Str0", Attitude.POSITIVE, IntData(70),
            null, null, 11, 21, 31, 41, 51f, 61.0, 'B', "Str1", Attitude.NEUTRAL, null,
            listOf(1, 2, 3),
            listOf(4, 5, null),
            listOf(listOf(Attitude.NEGATIVE, null)),
            listOf(IntData(1), IntData(2), IntData(3)),
            mutableListOf(IntData(1), null, IntData(3)),
            Tree("root", Tree("left"), Tree("right", Tree("right.left"), Tree("right.right"))),
            mapOf("one" to 1, "two" to 2, "three" to 3),
            mapOf(0 to null, 1 to "first", 2 to "second")
    )

    inline fun benchmark(warmup: Int = 2000, iterations: Int = 200_000, action: () -> Unit): Double {
        repeat(warmup) {
            action()
        }
        val start = System.currentTimeMillis()
        repeat(iterations) {
            action()
        }
        val end = System.currentTimeMillis()
        return (end - start).toDouble() / iterations
    }

    @Test
    fun testKotlinX() {
        val ser = Zoo.serializer()
        val ms = benchmark {
            val str = Json.plain.stringify(ser, instance)
            Json.plain.parse(ser, str)
        }
        println("KotlinX: $ms ms")
    }

    @Test
    fun testMirror() {
        val json = JsonSerializer(DefaultRegistry + TestRegistry)
        val type = Zoo::class.type
        val ms = benchmark {
            val str = json.write(instance, type)
            json.read(str, type)
        }
        println("Mirror: $ms ms")
    }
}