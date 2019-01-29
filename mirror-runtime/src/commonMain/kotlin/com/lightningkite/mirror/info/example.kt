package com.lightningkite.mirror.info

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlin.reflect.KClass

class PairClassInfo<A, B>(
        val typeA: MirrorType<A>,
        val typeB: MirrorType<B>
) : MirrorClass<Pair<A, B>>() {

    companion object {
        val minimal = PairClassInfo(AnyMirror, AnyMirror)
    }

    override val typeParameters: Array<MirrorType<*>>
        get() = arrayOf(typeA, typeB)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Pair<A, B>>
        get() = Pair::class as KClass<Pair<A, B>>
    override val modifiers: Array<Modifier>
        get() = arrayOf(Modifier.Data)
    override val packageName: String
        get() = "kotlin"
    override val localName: String
        get() = "Pair"
    override val owningClass: KClass<*>?
        get() = null
    override val companion: Any?
        get() = null
    override val annotations: List<Annotation>
        get() = listOf()

    val fieldFirst: Field<Pair<A, B>, A> = Field(
            owner = this,
            name = "first",
            type = typeA,
            isOptional = false,
            get = { it.first },
            set = null,
            annotations = listOf()
    )

    val fieldSecond: Field<Pair<A, B>, B> = Field(
            owner = this,
            name = "second",
            type = typeB,
            isOptional = false,
            get = { it.second },
            set = null,
            annotations = listOf()
    )

    override val fields: Array<Field<Pair<A, B>, *>> = arrayOf(
            fieldFirst,
            fieldSecond
    )

    override fun deserialize(decoder: Decoder): Pair<A, B> {
        var first: A? = null
        var second: B? = null
        decoder.beginStructure(this, typeA, typeB).apply {
            loop@ while (true) {
                when (decodeElementIndex(this@PairClassInfo)) {
                    CompositeDecoder.READ_ALL -> {
                        first = decodeSerializableElement(this@PairClassInfo, 0, typeA)
                        second = decodeSerializableElement(this@PairClassInfo, 1, typeB)
                        break@loop
                    }
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> first = decodeSerializableElement(this@PairClassInfo, 0, typeA)
                    1 -> second = decodeSerializableElement(this@PairClassInfo, 1, typeB)
                    else -> {
                    }
                }
            }
            endStructure(this@PairClassInfo)
        }
        return Pair(
                first = first as A,
                second = second as B
        )
    }

    override fun serialize(encoder: Encoder, obj: Pair<A, B>) {
        encoder.beginStructure(this, typeA, typeB).apply {
            encodeSerializableElement(this@PairClassInfo, 0, typeA, obj.first)
            encodeSerializableElement(this@PairClassInfo, 1, typeB, obj.second)
            endStructure(this@PairClassInfo)
        }
    }
}

/*
TODO

Reflective
Enums
Custom serializer for type through annotation?  @Serializer(forClass = Something::class)
Insure instance equivalency for MirrorTypes
Binary Data
Circular reference format

*/