package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialDescriptor

data class NullableMirrorType<Type : Any>(override val base: MirrorClass<Type>) : MirrorType<Type?>, SerialDescriptor by base {
    override val descriptor: SerialDescriptor get() = this

    override val isNullable: Boolean
        get() = true

    override fun serialize(encoder: Encoder, obj: Type?) {
        encoder.encodeNullableSerializableValue(
                serializer = base,
                value = obj
        )
    }

    override fun deserialize(decoder: Decoder): Type? {
        @Suppress("UNCHECKED_CAST")
        return decoder.decodeNullableSerializableValue(base as DeserializationStrategy<Type?>)
    }

    override fun patch(decoder: Decoder, old: Type?): Type? {
        @Suppress("UNCHECKED_CAST")
        return decoder.decodeNullableSerializableValue(base as DeserializationStrategy<Type?>)
    }
}