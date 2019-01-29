package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object CharMirror : MirrorClass<Char>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Char> get() = Char::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Char"
    override val fields: Array<Field<Char, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.CHAR
    override val companion: Any? get() = Char.Companion
    override fun deserialize(decoder: Decoder): Char = decoder.decodeChar()
    override fun serialize(encoder: Encoder, obj: Char) = encoder.encodeChar(obj)
}