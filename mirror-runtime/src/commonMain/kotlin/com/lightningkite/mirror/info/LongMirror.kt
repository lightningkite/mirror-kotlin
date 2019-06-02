package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object LongMirror : MirrorClass<Long>() {
    override val empty: Long
        get() = 0
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Long> get() = Long::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Long"
    override val fields: Array<Field<Long, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.LONG
    override val companion: Any? get() = Long.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(LongMirror))
    override fun deserialize(decoder: Decoder): Long = decoder.decodeLong()
    override fun serialize(encoder: Encoder, obj: Long) = encoder.encodeLong(obj)
}