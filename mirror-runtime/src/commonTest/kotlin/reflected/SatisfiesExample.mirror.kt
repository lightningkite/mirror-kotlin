//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

data class SatisfiesExampleMirror<T: Any?>(
    val TMirror: MirrorType<T>
) : PolymorphicMirror<SatisfiesExample<T>>() {
    
    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        override val minimal = SatisfiesExampleMirror(TypeArgumentMirrorType("T", AnyMirror.nullable))
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = SatisfiesExampleMirror(typeArguments[0] as MirrorType<Any?>)
    }
    
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<SatisfiesExample<T>> get() = SatisfiesExample::class as KClass<SatisfiesExample<T>>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Sealed)
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    override val packageName: String get() = "com.lightningkite.mirror.test"
    override val localName: String get() = "SatisfiesExample"
}
