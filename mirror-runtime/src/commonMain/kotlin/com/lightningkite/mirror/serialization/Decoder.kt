package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.isFrozen
import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

typealias TypeDecoder<IN, T> = IN.() -> T

interface Decoder<IN> {
    val registry: SerializationRegistry
    val arbitraryDecoders: MutableList<Generator<IN>>
    val kClassDecoders: MutableMap<KClass<*>, (Type<*>) -> TypeDecoder<IN, Any?>?>
    val decoders: MutableMap<Type<*>, TypeDecoder<IN, Any?>>

    @Suppress("UNCHECKED_CAST")
    fun initializeDecoders() {
        registry.decoderConfigurators.forEach { it.value.invoke(this as Decoder<Any?>) }
    }

    fun <T> addDecoder(type: Type<T>, action: TypeDecoder<IN, T>) {
        @Suppress("UNCHECKED_CAST")
        decoders[type] = action as IN.() -> Any?
    }

    fun <T : Any> addDecoder(kClass: KClass<T>, action: (Type<*>) -> TypeDecoder<IN, T>?) {
        @Suppress("UNCHECKED_CAST")
        kClassDecoders[kClass] = action as (Type<*>) -> (IN.() -> Any?)?
    }

    fun addDecoder(generator: Generator<IN>) {
        arbitraryDecoders.removeAll { it.description == generator.description }
        arbitraryDecoders.addSorted(generator) { a, b -> a.priority > b.priority }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> decoder(type: Type<T>): TypeDecoder<IN, T> = rawDecoder(type) as TypeDecoder<IN, T>

    @Suppress("UNCHECKED_CAST")
    fun rawDecoder(type: Type<*>): TypeDecoder<IN, Any?> =
            if (isFrozen) {
                decoders.getOrElse(type) {
                    kClassDecoders[type.kClass]?.invoke(type) ?: arbitraryDecoders.asSequence()
                            .mapNotNull { it.generateDecoder(type) }
                            .firstOrNull() ?: throw SerializationException("No decoder generated for $type!")
                }
            } else {
                decoders.getOrPut(type) {
                    kClassDecoders[type.kClass]?.invoke(type) ?: arbitraryDecoders.asSequence()
                            .mapNotNull { it.generateDecoder(type) }
                            .firstOrNull() ?: throw SerializationException("No decoder generated for $type!")
                }
            }

    interface Generator<IN> : SerializationGenerator {
        fun generateDecoder(type: Type<*>): TypeDecoder<IN, Any?>?
    }

    fun <T> decode(input: IN, type: Type<T>) = decoder(type).invoke(input)
}

inline fun <IN, T : Any> Decoder<IN>.setNotNullDecoder(kClass: KClass<T>, crossinline action: (Type<*>) -> TypeDecoder<IN, T>?) {
    @Suppress("UNCHECKED_CAST")
    kClassDecoders[kClass] = gen@{ type: Type<*> ->
        if (type.nullable) return@gen null
        else action(type)
    }
}