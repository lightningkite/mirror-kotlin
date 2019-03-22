//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object RemoteExceptionDataMirror : MirrorClass<RemoteExceptionData>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<RemoteExceptionData> get() = RemoteExceptionData::class as KClass<RemoteExceptionData>
    override val modifiers: Array<Modifier> get() = arrayOf()
    override val packageName: String get() = "com.lightningkite.mirror.request"
    override val localName: String get() = "RemoteExceptionData"
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    
    val fieldType: Field<RemoteExceptionData,String> = Field(
        owner = this,
            index = 0,
        name = "type",
        type = StringMirror,
        optional = true,
        get = { it.type },
        set = { it, value -> it.type = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldMessage: Field<RemoteExceptionData,String> = Field(
        owner = this,
            index = 1,
        name = "message",
        type = StringMirror,
        optional = true,
        get = { it.message },
        set = { it, value -> it.message = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldTrace: Field<RemoteExceptionData,String> = Field(
        owner = this,
            index = 2,
        name = "trace",
        type = StringMirror,
        optional = true,
        get = { it.trace },
        set = { it, value -> it.trace = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldData: Field<RemoteExceptionData,Any?> = Field(
        owner = this,
            index = 3,
        name = "data",
        type = AnyMirror.nullable,
        optional = true,
        get = { it.data },
        set = { it, value -> it.data = value },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<RemoteExceptionData, *>> = arrayOf(fieldType, fieldMessage, fieldTrace, fieldData)
    
    override fun deserialize(decoder: Decoder): RemoteExceptionData {
        var typeSet = false
        var fieldType: String? = null
        var messageSet = false
        var fieldMessage: String? = null
        var traceSet = false
        var fieldTrace: String? = null
        var dataSet = false
        var fieldData: Any? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldType = decoderStructure.decodeStringElement(this, 0)
                    typeSet = true
                    fieldMessage = decoderStructure.decodeStringElement(this, 1)
                    messageSet = true
                    fieldTrace = decoderStructure.decodeStringElement(this, 2)
                    traceSet = true
                    fieldData = decoderStructure.decodeSerializableElement(this, 3, AnyMirror.nullable)
                    dataSet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldType = decoderStructure.decodeStringElement(this, 0)
                    typeSet = true
                }
                1 -> {
                    fieldMessage = decoderStructure.decodeStringElement(this, 1)
                    messageSet = true
                }
                2 -> {
                    fieldTrace = decoderStructure.decodeStringElement(this, 2)
                    traceSet = true
                }
                3 -> {
                    fieldData = decoderStructure.decodeSerializableElement(this, 3, AnyMirror.nullable)
                    dataSet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!typeSet) {
            fieldType = ""
        }
        if(!messageSet) {
            fieldMessage = ""
        }
        if(!traceSet) {
            fieldTrace = ""
        }
        if(!dataSet) {
            fieldData = null
        }
        return RemoteExceptionData(
            type = fieldType as String,
            message = fieldMessage as String,
            trace = fieldTrace as String,
            data = fieldData as Any?
        )
    }
    
    override fun serialize(encoder: Encoder, obj: RemoteExceptionData) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeStringElement(this, 0, obj.type)
        encoderStructure.encodeStringElement(this, 1, obj.message)
        encoderStructure.encodeStringElement(this, 2, obj.trace)
        encoderStructure.encodeSerializableElement(this, 3, AnyMirror.nullable, obj.data)
        encoderStructure.endStructure(this)
    }
}
