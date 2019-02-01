package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.info.registerDefaults
import com.lightningkite.mirror.registerTest
import kotlin.test.Test
import kotlinx.serialization.json.Json

class PerformanceTest {

    init{
        registerDefaults()
        registerTest()
    }

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

    inline fun benchmark(warmup: Int = 20_000, iterations: Int = 200_000, action: () -> Unit): Double {
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
    fun testRoundTrip() {
        val kotlinx = Zoo.serializer()
        val mirror = ZooMirror
        val kotlinxMS = benchmark {
            val str = Json.plain.stringify(kotlinx, instance)
            Json.plain.parse(kotlinx, str)
        }
        println("Round Trip KotlinX: $kotlinxMS ms")
        val mirrorMs = benchmark {
            val str = Json.plain.stringify(mirror, instance)
            Json.plain.parse(mirror, str)
        }
        println("Round Trip Mirror: $mirrorMs ms")
        println("Round Trip Mirror/Kotlinx: ${mirrorMs/kotlinxMS}")
    }

    @Test
    fun testSerialize() {
        val kotlinx = Zoo.serializer()
        val mirror = ZooMirror
        val kotlinxMS = benchmark {
            Json.plain.stringify(kotlinx, instance)
        }
        println("Serialize KotlinX: $kotlinxMS ms")
        val mirrorMs = benchmark {
            Json.plain.stringify(mirror, instance)
        }
        println("Serialize Mirror: $mirrorMs ms")
        println("Serialize Mirror/Kotlinx: ${mirrorMs/kotlinxMS}")
    }

    @Test
    fun testDeserialize() {
        val kotlinx = Zoo.serializer()
        val mirror = ZooMirror
        val str = Json.plain.stringify(kotlinx, instance)
        val kotlinxMS = benchmark {
            Json.plain.parse(kotlinx, str)
        }
        println("Deserialize KotlinX: $kotlinxMS ms")
        val mirrorMs = benchmark {
            Json.plain.parse(mirror, str)
        }
        println("Deserialize Mirror: $mirrorMs ms")
        println("Deserialize Mirror/Kotlinx: ${mirrorMs/kotlinxMS}")
    }
}