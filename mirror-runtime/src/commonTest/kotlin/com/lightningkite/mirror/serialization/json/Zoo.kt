package com.lightningkite.mirror.serialization.json

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
        val mapIntStringN: Map<Int, String?>
)

enum class Attitude { POSITIVE, NEUTRAL, NEGATIVE }

@Serializable
data class IntData(val intV: Int)

@Serializable
data class Tree(val name: String, val left: Tree? = null, val right: Tree? = null)