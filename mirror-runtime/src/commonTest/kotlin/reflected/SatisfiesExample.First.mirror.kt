//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

data class SatisfiesExampleFirstMirror<T: Any?>(
    val TMirror: MirrorType<T>
) : MirrorClass<SatisfiesExample.First<T>>() {
    
    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        override val minimal = SatisfiesExampleFirstMirror(TypeArgumentMirrorType("T", AnyMirror.nullable))
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = SatisfiesExampleFirstMirror(typeArguments[0] as MirrorType<Any?>)
    }
    
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<SatisfiesExample.First<T>> get() = SatisfiesExample.First::class as KClass<SatisfiesExample.First<T>>
    override val modifiers: Array<Modifier> get() = arrayOf()
    override val packageName: String get() = "com.lightningkite.mirror.test"
    override val localName: String get() = "SatisfiesExample.First"
    override val implements: Array<MirrorClass<*>> get() = arrayOf(SatisfiesExampleMirror(TMirror))
    override val owningClass: KClass<*>? get() = SatisfiesExample::class
    
    override val fields: Array<Field<SatisfiesExample.First<T>, *>> = arrayOf()
    
    override fun deserialize(decoder: Decoder): SatisfiesExample.First<T> {
        val decoderStructure = decoder.beginStructure(this, TMirror)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        return SatisfiesExample.First<T>(
        )
    }
    
    override fun serialize(encoder: Encoder, obj: SatisfiesExample.First<T>) {
        val encoderStructure = encoder.beginStructure(this, TMirror)
        encoderStructure.endStructure(this)
    }
}
