package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

object CommonSerialization {

    val onEncoderSetup = ArrayList<(Encoder<Any?>) -> Unit>()
    val onDecoderSetup = ArrayList<(Decoder<Any?>) -> Unit>()

    init{
        onEncoderSetup += { coder ->
            coder.addEncoder(EnumCoderGenerators.EncoderGenerator(coder))
        }
        onDecoderSetup += { coder ->
            coder.addDecoder(EnumCoderGenerators.DecoderGenerator(coder))
        }
    }
}
