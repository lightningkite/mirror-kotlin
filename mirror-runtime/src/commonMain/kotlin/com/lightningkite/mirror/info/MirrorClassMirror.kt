package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

object MirrorClassMirror : MirrorClass<MirrorClass<*>>() {
    @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.Index", "com.lightningkite.mirror.info.MirrorRegistry"))
    class Index(
            val byName: Map<String, MirrorClass<*>>,
            val byClass: Map<KClass<*>, MirrorClass<*>>
    )

    @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.index", "com.lightningkite.mirror.info.MirrorRegistry"))
    val index
        get() = MirrorRegistry.index

    @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.register(mirror)", "com.lightningkite.mirror.info.MirrorRegistry"))
    fun register(vararg mirror: MirrorClass<*>) = MirrorRegistry.register(*mirror)

    @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.retrieve(any)", "com.lightningkite.mirror.info.MirrorRegistry"))
    fun retrieve(any: Any): MirrorClass<*> = MirrorRegistry.retrieve(any)

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<MirrorClass<*>> get() = MirrorClass::class
    override val packageName: String get() = "com.lightningkite.mirror.info"
    override val localName: String get() = "MirrorClass"
    override val fields: Array<Field<MirrorClass<*>, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING
    override val companion: Any? get() = null
    override fun deserialize(decoder: Decoder): MirrorClass<*> {
        val typeName = decoder.decodeString()
        return MirrorRegistry[typeName]
                ?: throw SerializationException("Unknown type name '$typeName', did you register it?")
    }

    override fun serialize(encoder: Encoder, obj: MirrorClass<*>) = encoder.encodeString(obj.name)
}

