package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.list
import kotlinx.serialization.set
import kotlin.reflect.KClass

data class SetMirror<E>(
        val typeE: MirrorType<E>
) : MirrorClass<Set<E>>(),
        KSerializer<Set<E>> by typeE.set,
        SerialDescriptor by typeE.set.descriptor {
    companion object {
        val minimal = SetMirror(AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(typeE)
    override val kClass: KClass<Set<E>> get() = Set::class as KClass<Set<E>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "Set"
    override val fields: Array<Field<Set<E>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}
