package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.set
import kotlin.reflect.KClass

data class SetMirror<E>(
        val EMirror: MirrorType<E>
) : MirrorClass<Set<E>>(),
        KSerializer<Set<E>> by EMirror.set,
        SerialDescriptor by EMirror.set.descriptor {
    override val mirrorClassCompanion: MirrorClassCompanion?
        get() = Companion

    companion object : MirrorClassCompanion {
        val EMirrorMinimal = TypeArgumentMirrorType("E", Variance.OUT, AnyMirror.nullable)
        override val minimal = SetMirror(EMirrorMinimal)
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = SetMirror(typeArguments[0])
        fun make(EMirror: MirrorType<*>? = null) = SetMirror(EMirror ?: EMirrorMinimal)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(EMirror)
    override val kClass: KClass<Set<E>> get() = Set::class as KClass<Set<E>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "Set"
    override val fields: Array<Field<Set<E>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}
