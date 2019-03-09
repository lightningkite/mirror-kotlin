package com.lightningkite.mirror.info

import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.native.freeze
import kotlinx.serialization.SerializationException
import kotlin.reflect.KClass

object MirrorRegistry {
    class Index(
            val byName: Map<String, MirrorClass<*>>,
            val byClass: Map<KClass<*>, MirrorClass<*>>
    )

    val index = AtomicReference<Index>(Index(mapOf(), mapOf()).freeze())
    fun register(vararg mirror: MirrorClass<*>) {
        val current = index.value
        index.value = Index(
                byName = current.byName + mirror.associateBy { it.name },
                byClass = current.byClass + mirror.associateBy { it.kClass }
        ).freeze()
    }

    operator fun <T : Any> get(kClass: KClass<T>): MirrorClass<T>? {
        @Suppress("UNCHECKED_CAST")
        return index.value.byClass[kClass] as? MirrorClass<T>
    }

    operator fun get(name: String): MirrorClass<*>? {
        @Suppress("UNCHECKED_CAST")
        return index.value.byName[name]
    }

    fun retrieve(any: Any): MirrorClass<*> = retrieveOrNull(any)
            ?: throw SerializationException("Cannot serialize ${any::class} because it is not registered.")

    fun retrieveOrNull(any: Any): MirrorClass<*>? {
        return index.value.byClass[any::class] ?: when (any) {
            is List<*> -> ListMirror.minimal
            is Set<*> -> SetMirror.minimal
            is Map<*, *> -> MapMirror.minimal
            else -> null
        }
    }

    init {
        register(
                UnitMirror,
                BooleanMirror,
                ByteMirror,
                ShortMirror,
                IntMirror,
                LongMirror,
                FloatMirror,
                DoubleMirror,
                CharMirror,
                StringMirror,
                MirrorClassMirror,
                MirrorClassFieldMirror.minimal,
                AnyMirror,
                ListMirror.minimal,
                MapMirror.minimal,
                SetMirror.minimal,
                ComparableMirror.minimal
        )
    }
}