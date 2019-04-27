package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.list
import kotlin.reflect.KClass

data class ListMirror<E>(
        val EMirror: MirrorType<E>
) : MirrorClass<List<E>>(),
        KSerializer<List<E>> by EMirror.list,
        SerialDescriptor by EMirror.list.descriptor {
    override val mirrorClassCompanion: MirrorClassCompanion?
        get() = Companion

    companion object: MirrorClassCompanion {
        override val minimal = ListMirror(TypeArgumentMirrorType("E", Variance.OUT, AnyMirror.nullable))
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = ListMirror(typeArguments[0])
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(EMirror)
    override val kClass: KClass<List<E>> get() = List::class as KClass<List<E>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "List"
    override val fields: Array<Field<List<E>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}