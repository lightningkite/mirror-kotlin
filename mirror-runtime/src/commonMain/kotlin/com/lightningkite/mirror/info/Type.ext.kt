@file:Suppress("UNCHECKED_CAST")

package com.lightningkite.mirror.info

val <T : Any> Type<T>.list get() = Type(List::class, listOf(TypeProjection(this))) as Type<List<T>>
val <T : Any> Type<T>.stringMap get() = Type(Map::class, listOf(TypeProjection(String::class.type), TypeProjection(this))) as Type<Map<String, T>>