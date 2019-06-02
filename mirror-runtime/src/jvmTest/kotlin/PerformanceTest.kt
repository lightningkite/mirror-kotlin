package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.info.registerDefaults
import com.lightningkite.mirror.registerTest
import com.lightningkite.mirror.test.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlin.test.Test
import kotlinx.serialization.json.Json

class PerformanceTest {

    init {
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

    data class Option(val name: String, val format: StringFormat, val serializer: KSerializer<Zoo>)

    val options = listOf(
            Option(name = "KotlinX Json", format = Json.plain, serializer = Zoo.serializer()),
            Option(name = "Mirror Json", format = Json.plain, serializer = ZooMirror)
    )

    inline fun benchmarkWithAllOptions(test: String, warmup: Int = 2_000, iterations: Int = 20_000, action: (StringFormat, KSerializer<Zoo>) -> Unit) {
        val results: Map<Option, Double> = options.associateWith { option ->
            benchmark(warmup, iterations) {
                action(option.format, option.serializer)
            }
        }
        val bestTime = results.values.min() ?: 1.0
        println("--Testing $test--")
        println(buildString {
            append("Option".padEnd(20))
            append("NS".padEnd(10))
            append("Ratio".padEnd(10))
        })
        results.entries.sortedBy { it.value }.forEach { (option, ms) ->
            println(buildString {
                append(option.name.padEnd(20))
                append(ms.times(1_000_000).toInt().toString().padEnd(10))
                append(ms.div(bestTime).toString().padEnd(10))
            })
        }
    }

    @Test
    fun testRoundTrip() {
        benchmarkWithAllOptions(
                test = "Round Trip"
        ) { format, ser ->
            val str = format.stringify(ser, instance)
            format.parse(ser, str)
        }
    }

    @Test
    fun testSerialize() {
        benchmarkWithAllOptions(
                test = "Serialize"
        ) { format, ser ->
            format.stringify(ser, instance)
        }
    }

    @Test
    fun testDeserialize() {
        val str = Json.plain.stringify(ZooMirror, instance)
        benchmarkWithAllOptions(
                test = "Deserialize"
        ) { format, ser ->
            format.parse(ser, str)
        }
    }
}