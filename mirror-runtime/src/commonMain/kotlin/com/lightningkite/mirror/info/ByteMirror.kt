package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object ByteMirror : MirrorClass<Byte>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Byte> get() = Byte::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Byte"
    override val fields: Array<Field<Byte, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.BYTE
    override val companion: Any? get() = Byte.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(ByteMirror))
    override fun deserialize(decoder: Decoder): Byte = decoder.decodeByte()
    override fun serialize(encoder: Encoder, obj: Byte) = encoder.encodeByte(obj)
}