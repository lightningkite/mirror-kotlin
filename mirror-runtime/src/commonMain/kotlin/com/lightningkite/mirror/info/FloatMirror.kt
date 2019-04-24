package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object FloatMirror : MirrorClass<Float>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Float> get() = Float::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Float"
    override val fields: Array<Field<Float, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.FLOAT
    override val companion: Any? get() = Float.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(FloatMirror))
    override fun deserialize(decoder: Decoder): Float = decoder.decodeFloat()
    override fun serialize(encoder: Encoder, obj: Float) = encoder.encodeFloat(obj)
}