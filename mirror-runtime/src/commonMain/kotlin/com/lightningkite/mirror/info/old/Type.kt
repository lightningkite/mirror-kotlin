package com.lightningkite.mirror.info

import kotlin.reflect.KClass

data class Type<T>(
        val kClass: KClass<*>,
        val typeParameters: List<TypeProjection> = listOf(),
        val nullable: Boolean = false
) {
    fun param(index: Int): TypeProjection = typeParameters.getOrElse(index) { TypeProjection.STAR }
}