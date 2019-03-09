package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialKind
import kotlinx.serialization.UnionKind

abstract class MirrorObject<T : Any>(val singleton: T) : MirrorClass<T>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val fields: Array<Field<T, *>> get() = arrayOf()
    override val kind: SerialKind get() = UnionKind.OBJECT
    override val companion: Any? get() = null
    override fun deserialize(decoder: Decoder): T {
        decoder.beginStructure(this).endStructure(this)
        return singleton
    }

    override fun serialize(encoder: Encoder, obj: T) {
        encoder.beginStructure(this).endStructure(this)
    }
}