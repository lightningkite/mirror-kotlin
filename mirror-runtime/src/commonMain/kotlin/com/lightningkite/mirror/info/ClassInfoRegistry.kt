package com.lightningkite.mirror.info

import com.lightningkite.kommon.collection.treeWalkDepthSequence
import kotlin.reflect.KClass

class ClassInfoRegistry(vararg infos: ClassInfo<*>) {
    val map:Map<KClass<*>, ClassInfo<*>> = infos.associate { it.kClass to it }
    val values get() = map.values
    val keys get() = map.keys
    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(kClass: KClass<T>):ClassInfo<T>? = map[kClass] as? ClassInfo<T>
    @Suppress("UNCHECKED_CAST")
    fun <T: Any> getOrThrow(kClass: KClass<T>):ClassInfo<T> = map[kClass] as? ClassInfo<T> ?: throw IllegalArgumentException("No class info registered for $kClass!")

    operator fun plus(other: ClassInfoRegistry):ClassInfoRegistry = ClassInfoRegistry(values + other.values)

    constructor(infos: List<ClassInfo<*>>):this(*infos.toTypedArray())
}

fun ClassInfo<*>.allImplements(registry: ClassInfoRegistry): Sequence<Type<*>>  {
    return implements.asSequence().treeWalkDepthSequence { registry[it.kClass]?.implements?.asSequence() ?: sequenceOf() }.distinct()
}