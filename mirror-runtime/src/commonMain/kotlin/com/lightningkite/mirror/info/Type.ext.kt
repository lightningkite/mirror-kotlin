package com.lightningkite.mirror.info

val <T : Any> Type<T>.list get() = Type(List::class, listOf(TypeProjection(this)))
val <T : Any> Type<T>.stringMap get() = Type(Map::class, listOf(TypeProjection(String::class.type), TypeProjection(this)))