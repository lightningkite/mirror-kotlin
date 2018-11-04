package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

interface Encoder<OUT> {
    val arbitraryEncoders: MutableList<Generator<OUT>>
    val kClassEncoders: MutableMap<KClass<*>, (Type<*>) -> (OUT.(value: Any?) -> Unit)?>
    val encoders: MutableMap<Type<*>, OUT.(value: Any?) -> Unit>

    fun <T : Any> addEncoder(type: Type<T>, action: OUT.(value: T?) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        encoders[type] = action as OUT.(value: Any?) -> Unit
    }

    fun <T : Any> addEncoder(kClass: KClass<T>, action: (Type<*>) -> (OUT.(value: T?) -> Unit)?) {
        @Suppress("UNCHECKED_CAST")
        kClassEncoders[kClass] = action as (Type<*>) -> (OUT.(value: Any?) -> Unit)?
    }

    fun addEncoder(generator: Generator<OUT>) {
        @Suppress("UNCHECKED_CAST")
        arbitraryEncoders.addSorted(generator) { a, b -> a.priority > b.priority }
    }


    fun <T : Any> encoder(type: Type<T>): OUT.(value: T?) -> Unit = rawEncoder(type)

    fun rawEncoder(type: Type<*>): OUT.(value: Any?) -> Unit =
            encoders.getOrPut(type) {
                kClassEncoders[type.kClass]?.invoke(type) ?: arbitraryEncoders.asSequence()
                        .mapNotNull { it.generateEncoder(type) }
                        .firstOrNull() ?: throw SerializationException("No encoder generated for $type!")
            }

    interface Generator<OUT> : Comparable<Generator<OUT>> {
        val priority: Float
        override fun compareTo(other: Generator<OUT>): Int = other.priority.compareTo(priority)
        fun generateEncoder(type: Type<*>): (OUT.(value: Any?) -> Unit)?
    }
}


inline fun <OUT, T : Any> Encoder<OUT>.setNotNullEncoder(kClass: KClass<T>, crossinline action: (Type<*>) -> (OUT.(value: T?) -> Unit)?) {
    @Suppress("UNCHECKED_CAST")
    kClassEncoders[kClass] = gen@{ type: Type<*> ->
        if (type.nullable) return@gen null
        else action(type)
    } as (Type<*>) -> (OUT.(value: Any?) -> Unit)?
}