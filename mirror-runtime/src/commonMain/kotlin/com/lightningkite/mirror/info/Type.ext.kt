@file:Suppress("UNCHECKED_CAST")

package com.lightningkite.mirror.info

import kotlin.reflect.KClass


val <T : Any> KClass<T>.type: Type<T>
    get() = Type(this)

val <T : Any> KClass<T>.typeNullable: Type<T?>
    get() = Type(this, nullable = true)

val <T : Any> ClassInfo<T>.type: Type<T>
    get() = Type(kClass)

val <T : Any> ClassInfo<T>.typeNullable: Type<T?>
    get() = Type(kClass, nullable = true)

val <T> Type<T>.list get() = Type<List<T>>(List::class, listOf(TypeProjection(this)))

val <T> Type<T>.stringMap get() = Type<Map<String, T>>(Map::class, listOf(TypeProjection(String::class.type), TypeProjection(this)))

infix fun <A, B> Type<A>.map(other:Type<B>):Type<Map<A, B>> = Type(Map::class, listOf(TypeProjection(this), TypeProjection(other)))