//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.lokalize.time

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object TimeStampMirror : MirrorClass<TimeStamp>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<TimeStamp> get() = TimeStamp::class as KClass<TimeStamp>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Inline)
    override val packageName: String get() = "com.lightningkite.lokalize.time"
    override val localName: String get() = "TimeStamp"
    override val implements: Array<MirrorClass<*>> get() = arrayOf(ComparableMirror(com.lightningkite.lokalize.time.TimeStampMirror))
    override val companion: Any? get() = TimeStamp.Companion
    
    val fieldMillisecondsSinceEpoch: Field<TimeStamp,kotlin.Long> = Field(
        owner = this,
            index = 0,
        name = "millisecondsSinceEpoch",
        type = LongMirror,
        optional = false,
        get = { it.millisecondsSinceEpoch },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<TimeStamp, *>> = arrayOf(fieldMillisecondsSinceEpoch)
    
    override fun deserialize(decoder: Decoder): TimeStamp {
        var millisecondsSinceEpochSet = false
        var fieldMillisecondsSinceEpoch: kotlin.Long? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldMillisecondsSinceEpoch = decoderStructure.decodeLongElement(this, 0)
                    millisecondsSinceEpochSet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldMillisecondsSinceEpoch = decoderStructure.decodeLongElement(this, 0)
                    millisecondsSinceEpochSet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!millisecondsSinceEpochSet) {
            throw MissingFieldException("millisecondsSinceEpoch")
        }
        return TimeStamp(
            millisecondsSinceEpoch = fieldMillisecondsSinceEpoch as kotlin.Long
        )
    }
    
    override fun serialize(encoder: Encoder, obj: TimeStamp) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeLongElement(this, 0, obj.millisecondsSinceEpoch)
        encoderStructure.endStructure(this)
    }
}
