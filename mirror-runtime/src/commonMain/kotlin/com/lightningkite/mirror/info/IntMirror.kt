package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object IntMirror : MirrorClass<Int>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Int> get() = Int::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Int"
    override val fields: Array<Field<Int, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.INT
    override val companion: Any? get() = Int.Companion
    override val implements: Array<MirrorClass<*>> get() = arrayOf(NumberMirror, ComparableMirror(IntMirror))
    override fun deserialize(decoder: Decoder): Int = decoder.decodeInt()
    override fun serialize(encoder: Encoder, obj: Int) = encoder.encodeInt(obj)
}