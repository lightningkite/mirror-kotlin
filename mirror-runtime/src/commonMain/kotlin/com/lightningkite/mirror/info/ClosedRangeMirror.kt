package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
data class ClosedRangeMirror<T: Comparable<T>>(
        val TMirror: MirrorType<T>
): MirrorClass<ClosedRange<T>>() {
    override val empty: ClosedRange<T>
        get() = TMirror.empty .. TMirror.empty

    override val mirrorClassCompanion: MirrorClassCompanion?
        get() = Companion

    companion object: MirrorClassCompanion {
        val TMirrorMinimal = TypeArgumentMirrorType("T", Variance.OUT, ComparableMirror(AnyMirror.nullable))
        override val minimal = ClosedRangeMirror(TMirrorMinimal)
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = ClosedRangeMirror(typeArguments[0] as MirrorType<Comparable<Comparable<*>>>)
        fun make(TMirror: MirrorType<*>? = null) = ClosedRangeMirror((TMirror ?: TMirrorMinimal) as MirrorType<Comparable<Comparable<*>>>)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TMirror)
    override val kClass: KClass<ClosedRange<T>> get() = ClosedRange::class as KClass<ClosedRange<T>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "List"
    override val companion: Any? get() = null

    val fieldStart: Field<ClosedRange<T>,T> = Field(
            owner = this,
            index = 0,
            name = "start",
            type = TMirror,
            optional = false,
            get = { it.start },
            annotations = listOf<Annotation>()
    )

    val fieldEndInclusive: Field<ClosedRange<T>,T> = Field(
            owner = this,
            index = 1,
            name = "endInclusive",
            type = TMirror,
            optional = false,
            get = { it.endInclusive },
            annotations = listOf<Annotation>()
    )

    override val fields: Array<Field<ClosedRange<T>, *>> = arrayOf(fieldStart, fieldEndInclusive)

    override fun deserialize(decoder: Decoder): ClosedRange<T> {
        var firstSet = false
        var fieldFirst: T? = null
        var secondSet = false
        var fieldSecond: T? = null
        val decoderStructure = decoder.beginStructure(this, TMirror, TMirror)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldFirst = decoderStructure.decodeSerializableElement(this, 0, TMirror)
                    firstSet = true
                    fieldSecond = decoderStructure.decodeSerializableElement(this, 1, TMirror)
                    secondSet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldFirst = decoderStructure.decodeSerializableElement(this, 0, TMirror)
                    firstSet = true
                }
                1 -> {
                    fieldSecond = decoderStructure.decodeSerializableElement(this, 1, TMirror)
                    secondSet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!firstSet) {
            throw MissingFieldException("first")
        }
        if(!secondSet) {
            throw MissingFieldException("second")
        }
        return fieldFirst!!.rangeTo<T>(fieldSecond!!)
    }

    override fun serialize(encoder: Encoder, obj: ClosedRange<T>) {
        val encoderStructure = encoder.beginStructure(this, TMirror, TMirror)
        encoderStructure.encodeSerializableElement(this, 0, TMirror, obj.start)
        encoderStructure.encodeSerializableElement(this, 1, TMirror, obj.endInclusive)
        encoderStructure.endStructure(this)
    }
}