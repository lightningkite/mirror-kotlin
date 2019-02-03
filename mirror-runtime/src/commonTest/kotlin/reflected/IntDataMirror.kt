//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.serialization.json

import kotlinx.serialization.Serializable
import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object IntDataMirror : MirrorClass<IntData>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<IntData> get() = IntData::class as KClass<IntData>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "com.lightningkite.mirror.serialization.json"
    override val localName: String get() = "IntData"
    override val annotations: List<Annotation> = listOf(SerializableMirror())
    
    val fieldIntV: Field<IntData,Int> = Field(
        owner = this,
        name = "intV",
        type = IntMirror,
        optional = false,
        get = { it.intV },
        annotations = listOf<Annotation>(SerializableMirror())
    )
    
    override val fields: Array<Field<IntData, *>> = arrayOf(fieldIntV)
    
    override fun deserialize(decoder: Decoder): IntData {
        var intVSet = false
        var fieldIntV: Int? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldIntV = decoderStructure.decodeIntElement(this, 0)
                    intVSet = true
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldIntV = decoderStructure.decodeIntElement(this, 0)
                    intVSet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!intVSet) {
            throw MissingFieldException("intV")
        }
        return IntData(
            intV = fieldIntV as Int
        )
    }
    
    override fun serialize(encoder: Encoder, obj: IntData) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeIntElement(this, 0, obj.intV)
        encoderStructure.endStructure(this)
    }
}