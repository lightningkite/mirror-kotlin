package com.lightningkite.mirror.info

data class TypeProjection(
        val type: Type<*>,
        val variance: Variance = Variance.INVARIANT
) {

    enum class Variance {
        INVARIANT,
        IN,
        OUT,
        STAR
    }

    companion object {
        val STAR = TypeProjection(Type<Any?>(Any::class, nullable = true), variance = Variance.STAR)
    }
}