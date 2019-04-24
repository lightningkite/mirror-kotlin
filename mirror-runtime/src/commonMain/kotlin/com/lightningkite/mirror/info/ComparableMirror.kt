package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.list
import kotlin.reflect.KClass

data class ComparableMirror<T>(
        val typeT: MirrorType<T>
) : PolymorphicMirror<Comparable<T>>() {

    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        override val minimal = ComparableMirror(TypeArgumentMirrorType("T", AnyMirror.nullable))
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = ComparableMirror(typeArguments[0])
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(typeT)
    override val kClass: KClass<Comparable<T>> get() = Comparable::class as KClass<Comparable<T>>
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Comparable"
    override val companion: Any? get() = null
}