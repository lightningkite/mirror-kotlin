//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package mirror.kotlin

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

data class PairMirror<A: Any?, B: Any?>(
    val AMirror: MirrorType<A>,
    val BMirror: MirrorType<B>
) : MirrorClass<Pair<A,B>>() {
    
    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        val AMirrorMinimal get() = AnyMirror.nullable
        val BMirrorMinimal get() = AnyMirror.nullable
        
        override val minimal = PairMirror(TypeArgumentMirrorType("A", Variance.OUT, AMirrorMinimal), TypeArgumentMirrorType("B", Variance.OUT, BMirrorMinimal))
        @Suppress("UNCHECKED_CAST")
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = PairMirror(typeArguments[0] as MirrorType<Any?>, typeArguments[1] as MirrorType<Any?>)
        
        @Suppress("UNCHECKED_CAST")
        fun make(
            AMirror: MirrorType<*>? = null,
            BMirror: MirrorType<*>? = null
        ) = PairMirror<Any?, Any?>(
            AMirror = (AMirror ?: AMirrorMinimal) as MirrorType<Any?>,
            BMirror = (BMirror ?: BMirrorMinimal) as MirrorType<Any?>
        )
    }
    
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(AMirror, BMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Pair<A,B>> get() = Pair::class as KClass<Pair<A,B>>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Pair"
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    
    val fieldFirst: Field<Pair<A,B>,A> = Field(
        owner = this,
        index = 0,
        name = "first",
        type = AMirror,
        optional = false,
        get = { it.first },
        annotations = listOf<Annotation>()
    )
    
    val fieldSecond: Field<Pair<A,B>,B> = Field(
        owner = this,
        index = 1,
        name = "second",
        type = BMirror,
        optional = false,
        get = { it.second },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<Pair<A,B>, *>> = arrayOf(fieldFirst, fieldSecond)
    
    override fun deserialize(decoder: Decoder): Pair<A,B> {
        var firstSet = false
        var fieldFirst: A? = null
        var secondSet = false
        var fieldSecond: B? = null
        val decoderStructure = decoder.beginStructure(this, AMirror, BMirror)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldFirst = decoderStructure.decodeSerializableElement(this, 0, AMirror)
                    firstSet = true
                    fieldSecond = decoderStructure.decodeSerializableElement(this, 1, BMirror)
                    secondSet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldFirst = decoderStructure.decodeSerializableElement(this, 0, AMirror)
                    firstSet = true
                }
                1 -> {
                    fieldSecond = decoderStructure.decodeSerializableElement(this, 1, BMirror)
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
        return Pair<A,B>(
            first = fieldFirst as A,
            second = fieldSecond as B
        )
    }
    
    override fun serialize(encoder: Encoder, obj: Pair<A,B>) {
        val encoderStructure = encoder.beginStructure(this, AMirror, BMirror)
        encoderStructure.encodeSerializableElement(this, 0, AMirror, obj.first)
        encoderStructure.encodeSerializableElement(this, 1, BMirror, obj.second)
        encoderStructure.endStructure(this)
    }
}
