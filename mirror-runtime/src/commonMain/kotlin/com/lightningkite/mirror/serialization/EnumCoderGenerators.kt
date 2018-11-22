package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.Type
import com.lightningkite.mirror.info.type
import com.lightningkite.mirror.info.typeNullable

object EnumCoderGenerators {
    class EncoderGenerator<OUT>(val encoder: Encoder<OUT>) : Encoder.Generator<OUT> {
        override val description: String get() = "Enum"
        override val priority: Float get() = .9f
        override fun generateEncoder(type: Type<*>): (OUT.(value: Any?) -> Unit)? {
            if(encoder.registry.classInfoRegistry[type.kClass]?.enumValues == null) return null
            val stringEncoder = encoder.encoder(String::class.type)
            return { value ->
                stringEncoder.invoke(this, (value as Enum<*>).name)
            }
        }
    }

    class DecoderGenerator<OUT>(val decoder: Decoder<OUT>) : Decoder.Generator<OUT> {
        override val description: String get() = "Enum"
        override val priority: Float get() = .9f
        override fun generateDecoder(type: Type<*>): (OUT.() -> Any?)? {
            @Suppress("UNCHECKED_CAST") val enumValues = decoder.registry.classInfoRegistry[type.kClass]?.enumValues as? List<Enum<*>> ?: return null
            val stringDecoder = decoder.decoder(String::class.type)
            return {
                val text = stringDecoder.invoke(this)
                val result = enumValues.find { text.equals(it.name, false) } ?: enumValues.first()
                result
            }
        }
    }
}
