package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object ShortMirror : MirrorClass<Short>() {
    override val empty: Short
        get() = 0
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Short> get() = Short::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Short"
    override val fields: Array<Field<Short, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.SHORT
    override val companion: Any? get() = Short.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(ShortMirror))
    override fun deserialize(decoder: Decoder): Short = decoder.decodeShort()
    override fun serialize(encoder: Encoder, obj: Short) = encoder.encodeShort(obj)
}