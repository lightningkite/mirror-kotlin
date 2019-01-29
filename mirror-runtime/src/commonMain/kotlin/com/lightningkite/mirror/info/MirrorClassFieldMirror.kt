package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

object MirrorClassFieldMirror: MirrorClass<MirrorClass.Field<*, *>>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Field<*, *>> get() = Field::class
    override val packageName: String get() = "com.lightningkite.mirror.info"
    override val localName: String get() = "MirrorClass.Field"
    override val fields: Array<Field<Field<*, *>, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING

    override fun deserialize(decoder: Decoder): Field<*, *> {
        val text = decoder.decodeString()
        val ownerName = text.substringBeforeLast('.')
        val fieldName = text.substringAfterLast('.')
        val owner = MirrorClassMirror.byName[ownerName] ?: throw SerializationException("Unknown type '$ownerName', did you register it?")
        val fieldIndex = owner.fieldsIndex[fieldName] ?: throw SerializationException("Type '$ownerName' has no field $fieldName")
        return owner.fields[fieldIndex]
    }

    override fun serialize(encoder: Encoder, obj: Field<*, *>) {
        encoder.encodeString(obj.owner.name + "." + obj.name)
    }

}