package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.isFrozen
import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

typealias TypeEncoder<OUT, T> = OUT.(value: T) -> Unit

interface Encoder<OUT> {
    val registry: SerializationRegistry
    val arbitraryEncoders: MutableList<Generator<OUT>>
    val kClassEncoders: MutableMap<KClass<*>, (Type<*>) -> TypeEncoder<OUT, Any?>?>
    val encoders: MutableMap<Type<*>, TypeEncoder<OUT, Any?>>

    @Suppress("UNCHECKED_CAST")
    fun initializeEncoders(){
        registry.encoderConfigurators.forEach { it.value.invoke(this as Encoder<Any?>) }
    }

    fun <T> addEncoder(type: Type<T>, action: TypeEncoder<OUT, T>) {
        @Suppress("UNCHECKED_CAST")
        encoders[type] = action as OUT.(value: Any?) -> Unit
    }

    fun <T : Any> addEncoder(kClass: KClass<T>, action: (Type<*>) -> (TypeEncoder<OUT, T>)?) {
        @Suppress("UNCHECKED_CAST")
        kClassEncoders[kClass] = action as (Type<*>) -> (OUT.(value: Any?) -> Unit)?
    }

    fun addEncoder(generator: Generator<OUT>) {
        arbitraryEncoders.removeAll { it.description == generator.description }
        arbitraryEncoders.addSorted(generator) { a, b -> a.priority > b.priority }
    }


    fun <T> encoder(type: Type<T>): TypeEncoder<OUT, T> = rawEncoder(type)

    fun rawEncoder(type: Type<*>): TypeEncoder<OUT, Any?> =
            if(isFrozen){
                encoders.getOrElse(type) {
                    kClassEncoders[type.kClass]?.invoke(type) ?: arbitraryEncoders.asSequence()
                            .mapNotNull { it.generateEncoder(type) }
                            .firstOrNull() ?: throw SerializationException("No encoder generated for $type!")
                }
            } else {
                encoders.getOrPut(type) {
                    kClassEncoders[type.kClass]?.invoke(type) ?: arbitraryEncoders.asSequence()
                            .mapNotNull { it.generateEncoder(type) }
                            .firstOrNull() ?: throw SerializationException("No encoder generated for $type!")
                }
            }

    interface Generator<OUT> : SerializationGenerator {
        fun generateEncoder(type: Type<*>): TypeEncoder<OUT, Any?>?
    }

    fun <T> encode(out: OUT, value: T, type: Type<T>) = encoder(type).invoke(out, value)
}

inline fun <OUT, T : Any> Encoder<OUT>.setNotNullEncoder(kClass: KClass<T>, crossinline action: (Type<*>) -> TypeEncoder<OUT, T>?) {
    @Suppress("UNCHECKED_CAST")
    kClassEncoders[kClass] = gen@{ type: Type<*> ->
        if (type.nullable) return@gen null
        else action(type)
    } as (Type<*>) -> TypeEncoder<OUT, Any?>?
}