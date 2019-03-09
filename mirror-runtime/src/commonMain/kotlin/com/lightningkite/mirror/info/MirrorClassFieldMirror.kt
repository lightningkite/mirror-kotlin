package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class MirrorClassFieldMirror<Owner, Value>(
        val OwnerMirror: MirrorType<Owner>,
        val ValueMirror: MirrorType<Value>
) : MirrorClass<MirrorClass.Field<Owner, Value>>() {

    companion object {
        val minimal = MirrorClassFieldMirror(AnyMirror, AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<MirrorClass.Field<Owner, Value>> get() = Field::class as KClass<Field<Owner, Value>>
    override val packageName: String get() = "com.lightningkite.mirror.info"
    override val localName: String get() = "MirrorClass.Field"
    override val fields: Array<Field<Field<Owner, Value>, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING

    override fun deserialize(decoder: Decoder): Field<Owner, Value> {
        val text = decoder.decodeString()
        val ownerName = text.substringBeforeLast('.')
        val fieldName = text.substringAfterLast('.')
        val owner = MirrorRegistry[ownerName]
                ?: throw SerializationException("Unknown type name '$ownerName', did you register it?")
        val fieldIndex = owner.fieldsIndex[fieldName] ?: throw SerializationException("Type '$ownerName' has no field $fieldName")
        return owner.fields[fieldIndex] as Field<Owner, Value>
    }

    override fun serialize(encoder: Encoder, obj: Field<Owner, Value>) {
        encoder.encodeString(obj.owner.name + "." + obj.name)
    }

}
