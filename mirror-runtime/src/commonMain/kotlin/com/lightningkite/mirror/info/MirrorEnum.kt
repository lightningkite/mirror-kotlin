package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialKind
import kotlinx.serialization.UnionKind
import kotlinx.serialization.internal.EnumDescriptor

abstract class MirrorEnum<T: Any>: MirrorClass<T>() {
    override val kind: SerialKind get() = UnionKind.ENUM_KIND
    abstract override val enumValues: Array<T>
    override val descriptor by lazy { EnumDescriptor(name, enumValues.map { (it as Enum<*>).name }.toTypedArray()) }
    override val fields: Array<Field<T, *>>
        get() = arrayOf()

    final override fun serialize(encoder: Encoder, obj: T) {
        val index = enumValues.indexOf(obj)
                .also { check(it != -1) { "$obj is not a valid enum $name, choices are $enumValues" } }
        encoder.encodeEnum(descriptor, index)
    }

    final override fun deserialize(decoder: Decoder): T {
        val index = decoder.decodeEnum(descriptor)
        check(index in enumValues.indices)
        { "$index is not among valid $name choices, choices are $enumValues" }
        return enumValues[index]
    }
}