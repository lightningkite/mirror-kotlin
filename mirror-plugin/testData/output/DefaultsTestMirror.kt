//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object DefaultsTestMirror : MirrorClass<DefaultsTest>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<DefaultsTest> get() = DefaultsTest::class as KClass<DefaultsTest>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "test"
    override val localName: String get() = "DefaultsTest"
    
    val fieldX: Field<DefaultsTest,Int> = Field(
        owner = this,
        name = "x",
        type = IntMirror,
        optional = true,
        get = { it.x },
        annotations = listOf<Annotation>()
    )
    
    val fieldY: Field<DefaultsTest,Float> = Field(
        owner = this,
        name = "y",
        type = FloatMirror,
        optional = true,
        get = { it.y },
        annotations = listOf<Annotation>()
    )
    
    val fieldNoDefault: Field<DefaultsTest,String> = Field(
        owner = this,
        name = "noDefault",
        type = StringMirror,
        optional = false,
        get = { it.noDefault },
        set = { it, value -> it.noDefault = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldZ: Field<DefaultsTest,String> = Field(
        owner = this,
        name = "z",
        type = StringMirror,
        optional = true,
        get = { it.z },
        set = { it, value -> it.z = value },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<DefaultsTest, *>> = arrayOf(fieldX, fieldY, fieldNoDefault, fieldZ)
    
    override fun deserialize(decoder: Decoder): DefaultsTest {
        var xSet = false
        var fieldX: Int? = null
        var ySet = false
        var fieldY: Float? = null
        var noDefaultSet = false
        var fieldNoDefault: String? = null
        var zSet = false
        var fieldZ: String? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldX = decoderStructure.decodeIntElement(this, 0)
                    xSet = true
                    fieldY = decoderStructure.decodeFloatElement(this, 1)
                    ySet = true
                    fieldNoDefault = decoderStructure.decodeStringElement(this, 2)
                    noDefaultSet = true
                    fieldZ = decoderStructure.decodeStringElement(this, 3)
                    zSet = true
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldX = decoderStructure.decodeIntElement(this, 0)
                    xSet = true
                }
                1 -> {
                    fieldY = decoderStructure.decodeFloatElement(this, 1)
                    ySet = true
                }
                2 -> {
                    fieldNoDefault = decoderStructure.decodeStringElement(this, 2)
                    noDefaultSet = true
                }
                3 -> {
                    fieldZ = decoderStructure.decodeStringElement(this, 3)
                    zSet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!xSet) {
            fieldX = 2
        }
        if(!ySet) {
            fieldY = .23f
        }
        if(!noDefaultSet) {
            throw MissingFieldException("noDefault")
        }
        if(!zSet) {
            fieldZ = "default"
        }
        return DefaultsTest(
            x = fieldX as Int,
            y = fieldY as Float,
            noDefault = fieldNoDefault as String,
            z = fieldZ as String
        )
    }
    
    override fun serialize(encoder: Encoder, obj: DefaultsTest) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeIntElement(this, 0, obj.x)
        encoderStructure.encodeFloatElement(this, 1, obj.y)
        encoderStructure.encodeStringElement(this, 2, obj.noDefault)
        encoderStructure.encodeStringElement(this, 3, obj.z)
        encoderStructure.endStructure(this)
    }
}
