package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object BooleanMirror : MirrorClass<Boolean>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Boolean> get() = Boolean::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Boolean"
    override val fields: Array<Field<Boolean, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.BOOLEAN
    override val companion: Any? get() = Boolean.Companion
    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeBoolean()
    override fun serialize(encoder: Encoder, obj: Boolean) = encoder.encodeBoolean(obj)
    override val enumValues: Array<Boolean>?
        get() = arrayOf(false, true)
}