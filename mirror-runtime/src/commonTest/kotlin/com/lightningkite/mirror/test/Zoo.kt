package com.lightningkite.mirror.test

import kotlinx.serialization.Serializable

@Serializable
data class Zoo(
        val unit: Unit,
        val boolean: Boolean,
        val byte: Byte,
        val short: Short,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double,
        val char: Char,
        val string: String,
        val enum: Attitude,
        val intData: IntData,
        val unitN: Unit?,
        val booleanN: Boolean?,
        val byteN: Byte?,
        val shortN: Short?,
        val intN: Int?,
        val longN: Long?,
        val floatN: Float?,
        val doubleN: Double?,
        val charN: Char?,
        val stringN: String?,
        val enumN: Attitude?,
        val intDataN: IntData?,
        val listInt: List<Int>,
        val listIntN: List<Int?>,
        val listListEnumN: List<List<Attitude?>>,
        val listIntData: List<IntData>,
        val listIntDataN: List<IntData?>,
        val tree: Tree,
        val mapStringInt: Map<String, Int>,
        val mapIntStringN: Map<Int, String?>,
        val defaultIfNotPresent: Int = 42
) {
    companion object {
        fun instance() = Zoo(
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
    }
}

enum class Attitude { POSITIVE, NEUTRAL, NEGATIVE }

@Serializable
data class IntData(val intV: Int)

@Serializable
data class Tree(val name: String, val left: Tree? = null, val right: Tree? = null)