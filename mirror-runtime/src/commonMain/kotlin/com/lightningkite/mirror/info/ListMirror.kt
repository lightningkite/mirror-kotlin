package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.list
import kotlin.reflect.KClass

data class ListMirror<E>(
        val typeE: MirrorType<E>
) : MirrorClass<List<E>>(),
        KSerializer<List<E>> by typeE.list,
        SerialDescriptor by typeE.list.descriptor {
    companion object {
        val minimal = ListMirror(AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(typeE)
    override val kClass: KClass<List<E>> get() = List::class as KClass<List<E>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "List"
    override val fields: Array<Field<List<E>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}