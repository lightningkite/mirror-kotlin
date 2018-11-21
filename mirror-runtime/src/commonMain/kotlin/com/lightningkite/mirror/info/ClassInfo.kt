package com.lightningkite.mirror.info

import kotlin.reflect.KClass

interface ClassInfo<T : Any> {
    val kClass: KClass<T>
    val modifiers: List<Modifier>

    val implements: List<Type<*>>

    val packageName: String
    val ownerName: String?
    val owner: KClass<*>?
    val name: String

    val annotations: List<AnnotationInfo>
    val enumValues: List<T>?

    val fields: List<FieldInfo<T, *>>

    fun construct(map: Map<String, Any?>): T

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface
    }
}