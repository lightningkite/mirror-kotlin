package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

object MirrorClassMirror : MirrorClass<MirrorClass<*>>() {
    //TODO: Make compatible with native
    val byName = HashMap<String, MirrorClass<*>>()
    val byClass = HashMap<KClass<*>, MirrorClass<*>>()
    fun register(vararg mirror: MirrorClass<*>) {
        for (m in mirror) {
            byName[m.name] = m
            byClass[m.kClass] = m
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
        return byName[typeName] ?: throw SerializationException("Unknown type '$typeName', did you register it?")
    }

    override fun serialize(encoder: Encoder, obj: MirrorClass<*>) = encoder.encodeString(obj.name)
}