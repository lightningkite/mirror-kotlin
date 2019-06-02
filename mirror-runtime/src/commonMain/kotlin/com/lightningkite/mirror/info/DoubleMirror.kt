package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object DoubleMirror : MirrorClass<Double>() {
    override val empty: Double
        get() = 0.0
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Double> get() = Double::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Double"
    override val fields: Array<Field<Double, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.DOUBLE
    override val companion: Any? get() = Double.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(DoubleMirror))
    override fun deserialize(decoder: Decoder): Double = decoder.decodeDouble()
    override fun serialize(encoder: Encoder, obj: Double) = encoder.encodeDouble(obj)
}