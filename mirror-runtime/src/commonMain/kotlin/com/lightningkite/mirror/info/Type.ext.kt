@file:Suppress("UNCHECKED_CAST")

package com.lightningkite.mirror.info

val <T> Type<T>.list get() = Type<List<T>>(List::class, listOf(TypeProjection(this)))
val <T> Type<T>.stringMap get() = Type<Map<String, T>>(Map::class, listOf(TypeProjection(String::class.type), TypeProjection(this)))