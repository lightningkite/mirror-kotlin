//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package mirror.kotlin

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

data class TripleMirror<A: Any?, B: Any?, C: Any?>(
    val AMirror: MirrorType<A>,
    val BMirror: MirrorType<B>,
    val CMirror: MirrorType<C>
) : MirrorClass<Triple<A,B,C>>() {
    
    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        val AMirrorMinimal get() = AnyMirror.nullable
        val BMirrorMinimal get() = AnyMirror.nullable
        val CMirrorMinimal get() = AnyMirror.nullable
        
        override val minimal = TripleMirror(TypeArgumentMirrorType("A", Variance.OUT, AMirrorMinimal), TypeArgumentMirrorType("B", Variance.OUT, BMirrorMinimal), TypeArgumentMirrorType("C", Variance.OUT, CMirrorMinimal))
        @Suppress("UNCHECKED_CAST")
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = TripleMirror(typeArguments[0] as MirrorType<Any?>, typeArguments[1] as MirrorType<Any?>, typeArguments[2] as MirrorType<Any?>)
        
        @Suppress("UNCHECKED_CAST")
        fun make(
            AMirror: MirrorType<*>? = null,
            BMirror: MirrorType<*>? = null,
            CMirror: MirrorType<*>? = null
        ) = TripleMirror<Any?, Any?, Any?>(
            AMirror = (AMirror ?: AMirrorMinimal) as MirrorType<Any?>,
            BMirror = (BMirror ?: BMirrorMinimal) as MirrorType<Any?>,
            CMirror = (CMirror ?: CMirrorMinimal) as MirrorType<Any?>
        )
    }
    
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(AMirror, BMirror, CMirror)
    override val empty: Triple<A,B,C> get() = Triple(
        first = AMirror.empty,
        second = BMirror.empty,
        third = CMirror.empty
    )
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Triple<A,B,C>> get() = Triple::class as KClass<Triple<A,B,C>>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Triple"
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    
    val fieldFirst: Field<Triple<A,B,C>,A> = Field(
        owner = this,
        index = 0,
        name = "first",
        type = AMirror,
        optional = false,
        get = { it.first },
        annotations = listOf<Annotation>()
    )
    
    val fieldSecond: Field<Triple<A,B,C>,B> = Field(
        owner = this,
        index = 1,
        name = "second",
        type = BMirror,
        optional = false,
        get = { it.second },
        annotations = listOf<Annotation>()
    )
    
    val fieldThird: Field<Triple<A,B,C>,C> = Field(
        owner = this,
        index = 2,
        name = "third",
        type = CMirror,
        optional = false,
        get = { it.third },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<Triple<A,B,C>, *>> = arrayOf(fieldFirst, fieldSecond, fieldThird)
    
    override fun deserialize(decoder: Decoder): Triple<A,B,C> {
        var firstSet = false
        var fieldFirst: A? = null
        var secondSet = false
        var fieldSecond: B? = null
        var thirdSet = false
        var fieldThird: C? = null
        val decoderStructure = decoder.beginStructure(this, AMirror, BMirror, CMirror)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldFirst = decoderStructure.decodeSerializableElement(this, 0, AMirror)
                    firstSet = true
                    fieldSecond = decoderStructure.decodeSerializableElement(this, 1, BMirror)
                    secondSet = true
                    fieldThird = decoderStructure.decodeSerializableElement(this, 2, CMirror)
                    thirdSet = true
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
                2 -> {
                    fieldThird = decoderStructure.decodeSerializableElement(this, 2, CMirror)
                    thirdSet = true
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
        if(!thirdSet) {
            throw MissingFieldException("third")
        }
        return Triple<A,B,C>(
            first = fieldFirst as A,
            second = fieldSecond as B,
            third = fieldThird as C
        )
    }
    
    override fun serialize(encoder: Encoder, obj: Triple<A,B,C>) {
        val encoderStructure = encoder.beginStructure(this, AMirror, BMirror, CMirror)
        encoderStructure.encodeSerializableElement(this, 0, AMirror, obj.first)
        encoderStructure.encodeSerializableElement(this, 1, BMirror, obj.second)
        encoderStructure.encodeSerializableElement(this, 2, CMirror, obj.third)
        encoderStructure.endStructure(this)
    }
}
