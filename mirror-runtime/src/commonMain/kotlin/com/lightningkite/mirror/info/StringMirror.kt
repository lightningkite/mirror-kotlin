package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object StringMirror : MirrorClass<String>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<String> get() = String::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "String"
    override val fields: Array<Field<String, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING
    override val companion: Any? get() = String.Companion
    override fun deserialize(decoder: Decoder): String = decoder.decodeString()
    override fun serialize(encoder: Encoder, obj: String) = encoder.encodeString(obj)
}