package com.lightningkite.mirror.info

import kotlinx.serialization.*

abstract class PolymorphicMirror<T : Any> : MirrorClass<T>() {
    override val empty: T
        get() = (MirrorRegistry.firstSatisfying(this) ?: throw IllegalStateException("No types satisfying this one found")).empty as T
    override val fields: Array<Field<T, *>>
        get() = arrayOf(
                Field(
                        owner = this,
                        index = 0,
                        name = "type",
                        type = MirrorClassMirror.minimal,
                        optional = false,
                        get = {
                            @Suppress("UNCHECKED_CAST")
                            MirrorRegistry.retrieve(it) as MirrorClass<Any>
                        },
                        set = null,
                        annotations = listOf()
                ),
                Field(
                        owner = this,
                        index = 1,
                        name = "value",
                        type = AnyMirror,
                        optional = false,
                        get = { it },
                        set = null,
                        annotations = listOf()
                )
        )
    override val kind: SerialKind get() = UnionKind.POLYMORPHIC
    override fun deserialize(decoder: Decoder): T {
        var mirror: MirrorClass<*>? = null
        var value: T? = null
        decoder.beginStructure(this).apply {
            loop@ while (true) {
                @Suppress("UNCHECKED_CAST")
                when (decodeElementIndex(this@PolymorphicMirror)) {
                    CompositeDecoder.READ_ALL -> {
                        mirror = decodeSerializableElement(this@PolymorphicMirror, 0, MirrorClassMirror.minimal)
                        value = decodeSerializableElement(this@PolymorphicMirror, 1, mirror!!) as T
                        break@loop
                    }
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> mirror = decodeSerializableElement(this@PolymorphicMirror, 0, MirrorClassMirror.minimal)
                    1 -> value = decodeSerializableElement(this@PolymorphicMirror, 1, mirror!!) as T
                    else -> {
                    }
                }
            }
            endStructure(this@PolymorphicMirror)
        }
        return requireNotNull(value)
    }

    override fun serialize(encoder: Encoder, obj: T) {
        val struct = encoder.beginStructure(this)
        val mirror = MirrorRegistry.retrieve(obj)
        @Suppress("UNCHECKED_CAST")
        struct.encodeSerializableElement(
                desc = this,
                index = 0,
                serializer = MirrorClassMirror.minimal,
                value = mirror.base as MirrorClass<Any>
        )
        @Suppress("UNCHECKED_CAST")
        struct.encodeSerializableElement(
                desc = this,
                index = 1,
                serializer = mirror as KSerializer<Any>,
                value = obj
        )
        struct.endStructure(this@PolymorphicMirror)
    }
}