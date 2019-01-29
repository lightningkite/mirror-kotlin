package com.lightningkite.mirror.representation

import com.fasterxml.jackson.annotation.JsonIgnore

data class ReadTypeProjection(
        val type: ReadType = ReadType(),
        val variance: Variance = Variance.INVARIANT
) {
    enum class Variance {
        IN, OUT, INVARIANT, STAR
    }

    val use: String
        @JsonIgnore get() = when (variance) {
            Variance.IN -> "in " + type.use
            Variance.OUT -> "out " + type.use
            Variance.INVARIANT -> type.use
            Variance.STAR -> "*"
        }

}