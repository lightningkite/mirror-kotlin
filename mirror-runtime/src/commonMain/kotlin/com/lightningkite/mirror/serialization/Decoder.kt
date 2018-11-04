package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

interface Decoder<IN> {
    val arbitraryDecoders: MutableList<Generator<IN>>
    val kClassDecoders: MutableMap<KClass<*>, (Type<*>) -> (IN.() -> Any?)?>
    val decoders: MutableMap<Type<*>, IN.() -> Any?>

    fun <T : Any> addDecoder(type: Type<T>, action: IN.() -> T?) {
        @Suppress("UNCHECKED_CAST")
        decoders[type] = action as IN.() -> Any?
    }

    fun <T : Any> addDecoder(kClass: KClass<T>, action: (Type<*>) -> (IN.() -> T?)?) {
        @Suppress("UNCHECKED_CAST")
        kClassDecoders[kClass] = action as (Type<*>) -> (IN.() -> Any?)?
    }

    fun addDecoder(generator: Generator<IN>) {
        @Suppress("UNCHECKED_CAST")
        arbitraryDecoders.addSorted(generator) { a, b -> a.priority > b.priority }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> decoder(type: Type<T>): IN.() -> T? = rawDecoder(type) as (IN.() -> T?)

    @Suppress("UNCHECKED_CAST")
    fun rawDecoder(type: Type<*>): IN.() -> Any? =
            decoders.getOrPut(type) {
                kClassDecoders[type.kClass]?.invoke(type) ?: arbitraryDecoders.asSequence()
                        .mapNotNull { it.generateDecoder(type) }
                        .firstOrNull() ?: throw SerializationException("No decoder generated for $type!")
            }

    interface Generator<IN> : Comparable<Generator<IN>> {
        val priority: Float
        override fun compareTo(other: Generator<IN>): Int = other.priority.compareTo(priority)
        fun generateDecoder(type: Type<*>): (IN.() -> Any?)?
    }
}

inline fun <IN, T : Any> Decoder<IN>.setNotNullDecoder(kClass: KClass<T>, crossinline action: (Type<*>) -> (IN.() -> T?)?) {
    @Suppress("UNCHECKED_CAST")
    kClassDecoders[kClass] = gen@{ type: Type<*> ->
        if (type.nullable) return@gen null
        else action(type)
    } as (Type<*>) -> (IN.() -> Any?)?
}