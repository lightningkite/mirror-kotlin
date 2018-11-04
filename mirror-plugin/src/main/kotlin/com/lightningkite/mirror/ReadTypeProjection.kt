package com.lightningkite.mirror

data class ReadTypeProjection(
        val type: ReadType,
        val variance: Variance
) {
    enum class Variance {
        IN, OUT, EXACT, STAR
    }

    val use: String
        get() = when (variance) {
            Variance.IN -> "in " + type.use
            Variance.OUT -> "out " + type.use
            Variance.EXACT -> type.use
            Variance.STAR -> "*"
        }

    override fun toString(): String = "TypeProjection($type, TypeProjection.Variance.$variance)"
}