package com.lightningkite.mirror.info

import kotlin.reflect.KClass

data class Type<T : Any>(
        val kClass: KClass<T>,
        val typeParameters: List<TypeProjection> = listOf(),
        val nullable: Boolean = false
) {
    fun param(index: Int): TypeProjection = typeParameters.getOrElse(index) { TypeProjection.STAR }
}