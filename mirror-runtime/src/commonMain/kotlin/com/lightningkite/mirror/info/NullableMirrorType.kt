package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialDescriptor

data class NullableMirrorType<Type : Any>(override val base: MirrorClass<Type>) : MirrorType<Type?>, SerialDescriptor by base {
    override val descriptor: SerialDescriptor get() = this

    override val isNullable: Boolean
        get() = true

    override fun serialize(encoder: Encoder, obj: Type?) {
        if (obj != null) {
            encoder.encodeNotNullMark()
            base.serialize(encoder, obj)
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): Type? = if (decoder.decodeNotNullMark()) base.deserialize(decoder) else decoder.decodeNull()

    override fun patch(decoder: Decoder, old: Type?): Type? {
        return when {
            old == null -> deserialize(decoder)
            decoder.decodeNotNullMark() -> base.patch(decoder, old)
            else -> decoder.decodeNull().let { old }
        }
    }
}