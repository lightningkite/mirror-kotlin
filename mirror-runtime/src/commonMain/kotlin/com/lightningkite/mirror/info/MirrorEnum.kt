package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialKind
import kotlinx.serialization.UnionKind
import kotlinx.serialization.internal.EnumDescriptor

abstract class MirrorEnum<T: Any>: MirrorClass<T>() {
    override val kind: SerialKind get() = UnionKind.ENUM_KIND
    abstract override val enumValues: Array<T>
    override val descriptor by lazy { MirrorEnum.Descriptor(this) }
    override val fields: Array<Field<T, *>>
        get() = arrayOf()

    class Descriptor<T : Any>(val parent: MirrorEnum<T>) : EnumDescriptor(parent.name, parent.enumValues.map { (it as Enum<*>).name }.toTypedArray())

    final override fun serialize(encoder: Encoder, obj: T) {
        val index = enumValues.indexOf(obj)
                .also { check(it != -1) { "$obj is not a valid enum $name, choices are $enumValues" } }
        encoder.encodeEnum(descriptor, index)
    }

    final override fun deserialize(decoder: Decoder): T {
        val index = decoder.decodeEnum(descriptor)
        check(index in enumValues.indices)
        { "$index is not among valid $name choices, choices size is ${enumValues.size}" }
        return enumValues[index]
    }
}