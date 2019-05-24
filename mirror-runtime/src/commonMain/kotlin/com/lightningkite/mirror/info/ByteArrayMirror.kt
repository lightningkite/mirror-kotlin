package com.lightningkite.mirror.info

import com.lightningkite.kommon.bytes.decodeBase64
import com.lightningkite.kommon.bytes.encodeBase64
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object ByteArrayMirror : MirrorClass<ByteArray>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<ByteArray> get() = ByteArray::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "ByteArray"
    override val fields: Array<Field<ByteArray, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING
    override val companion: Any? get() = null
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    override fun deserialize(decoder: Decoder): ByteArray = decoder.decodeString().decodeBase64()
    override fun serialize(encoder: Encoder, obj: ByteArray) = encoder.encodeString(obj.encodeBase64())
}