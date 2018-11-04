package com.lightningkite.mirror.info

import kotlin.reflect.KClass

open class EmptyClassInfo<T : Any>(
        override val kClass: KClass<T>,
        override val packageName: String = "",
        override val name: String = ""
) : ClassInfo<T> {
    override val implements: List<Type<*>>
        get() = listOf()
    override val owner: KClass<*>?
        get() = null
    override val ownerName: String?
        get() = null
    override val modifiers: List<ClassInfo.Modifier>
        get() = listOf()
    override val annotations: List<AnnotationInfo>
        get() = listOf()
    override val enumValues: List<T>?
        get() = null
    override val fields: List<SerializedFieldInfo<T, *>>
        get() = listOf()

    override fun construct(map: Map<String, Any?>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}