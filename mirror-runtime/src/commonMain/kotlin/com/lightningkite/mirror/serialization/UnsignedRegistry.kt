@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@SharedImmutable
val UnsignedRegistry = SerializationRegistry(
        classInfoRegistry = UnsignedClassInfoRegistry,
        encoderConfigurators = mapOf(
                "unsigned" to { it: Encoder<Any?> ->
                    run{
                        val parent = it.encoder(Byte::class.type)
                        it.addEncoder(UByte::class.type){
                            parent.invoke(this, it.toByte())
                        }
                    }
                    run{
                        val parent = it.encoder(Short::class.type)
                        it.addEncoder(UShort::class.type){
                            parent.invoke(this, it.toShort())
                        }
                    }
                    run{
                        val parent = it.encoder(Int::class.type)
                        it.addEncoder(UInt::class.type){
                            parent.invoke(this, it.toInt())
                        }
                    }
                    run{
                        val parent = it.encoder(Long::class.type)
                        it.addEncoder(ULong::class.type){
                            parent.invoke(this, it.toLong())
                        }
                    }
                }
        ),
        decoderConfigurators = mapOf(
                "unsigned" to { it: Decoder<Any?> ->
                    run{
                        val parent = it.decoder(Byte::class.type)
                        it.addDecoder(UByte::class.type){
                            parent.invoke(this).toUByte()
                        }
                    }
                    run{
                        val parent = it.decoder(Short::class.type)
                        it.addDecoder(UShort::class.type){
                            parent.invoke(this).toUShort()
                        }
                    }
                    run{
                        val parent = it.decoder(Int::class.type)
                        it.addDecoder(UInt::class.type){
                            parent.invoke(this).toUInt()
                        }
                    }
                    run{
                        val parent = it.decoder(Long::class.type)
                        it.addDecoder(ULong::class.type){
                            parent.invoke(this).toULong()
                        }
                    }
                }
        )
)