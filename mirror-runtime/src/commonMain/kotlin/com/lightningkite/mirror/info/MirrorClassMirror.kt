package com.lightningkite.mirror.info

import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.native.freeze
import kotlinx.serialization.*
import kotlin.native.concurrent.SharedImmutable
import kotlin.reflect.KClass

object MirrorClassMirror : MirrorClass<MirrorClass<*>>() {
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

    fun retrieve(any: Any): MirrorClass<*> {
        return index.value.byClass[any::class] ?: when (any) {
            is List<*> -> ListMirror.minimal
            is Map<*, *> -> MapMirror.minimal
            else -> throw SerializationException("Cannot serialize ${any::class} because it is not registered.")
        }
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<MirrorClass<*>> get() = MirrorClass::class
    override val packageName: String get() = "com.lightningkite.mirror.info"
    override val localName: String get() = "MirrorClass"
    override val fields: Array<Field<MirrorClass<*>, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING
    override val companion: Any? get() = null
    override fun deserialize(decoder: Decoder): MirrorClass<*> {
        val typeName = decoder.decodeString()
        return index.value.byName[typeName]
                ?: throw SerializationException("Unknown type name '$typeName', did you register it?")
    }

    override fun serialize(encoder: Encoder, obj: MirrorClass<*>) = encoder.encodeString(obj.name)
}
