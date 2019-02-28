//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object AnnotatedClassMirror : MirrorClass<AnnotatedClass>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<AnnotatedClass> get() = AnnotatedClass::class as KClass<AnnotatedClass>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "test"
    override val localName: String get() = "AnnotatedClass"
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    override val annotations: List<Annotation> = listOf(CustomAnnotationMirror())
    
    val fieldY: Field<AnnotatedClass,String> = Field(
        owner = this,
        name = "y",
        type = StringMirror,
        optional = true,
        get = { it.y },
        annotations = listOf<Annotation>(CustomAnnotationMirror())
    )
    
    override val fields: Array<Field<AnnotatedClass, *>> = arrayOf(fieldY)
    
    override fun deserialize(decoder: Decoder): AnnotatedClass {
        var ySet = false
        var fieldY: String? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldY = decoderStructure.decodeStringElement(this, 0)
                    ySet = true
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldY = decoderStructure.decodeStringElement(this, 0)
                    ySet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!ySet) {
            fieldY = ""
        }
        return AnnotatedClass(
            y = fieldY as String
        )
    }
    
    override fun serialize(encoder: Encoder, obj: AnnotatedClass) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeStringElement(this, 0, obj.y)
        encoderStructure.endStructure(this)
    }
}
