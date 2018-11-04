package com.lightningkite.mirror.info

data class TypeProjection(
        val type: Type<*>,
        val variance: Variance = Variance.INVARIANT,
        val isStar: Boolean = false
) {

    enum class Variance {
        INVARIANT,
        IN,
        OUT,
    }

    companion object {
        val STAR = TypeProjection(Type(Any::class, nullable = true), isStar = true)
    }
}