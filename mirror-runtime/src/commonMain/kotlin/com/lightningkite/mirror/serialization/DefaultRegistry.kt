package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.PrimitiveClassInfoRegistry

@SharedImmutable
val DefaultRegistry = SerializationRegistry(
        classInfoRegistry = PrimitiveClassInfoRegistry,
        encoderConfigurators = mapOf(
                "enum" to { it: Encoder<Any?> ->
                    it.addEncoder(EnumCoderGenerators.EncoderGenerator(it))
                }
        ),
        decoderConfigurators = mapOf(
                "enum" to { it: Decoder<Any?> ->
                    it.addDecoder(EnumCoderGenerators.DecoderGenerator(it))
                }
        )
)