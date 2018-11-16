package com.lightningkite.mirror

data class ReadTypeProjection(
        val type: ReadType,
        val variance: Variance
) {
    enum class Variance {
        IN, OUT, INVARIANT, STAR
    }

    val use: String
        get() = when (variance) {
            Variance.IN -> "in " + type.use
            Variance.OUT -> "out " + type.use
            Variance.INVARIANT -> type.use
            Variance.STAR -> "*"
        }

    fun toString(owner: ReadClassInfo): String = "TypeProjection(${type.toString(owner)}, TypeProjection.Variance.$variance)"
}