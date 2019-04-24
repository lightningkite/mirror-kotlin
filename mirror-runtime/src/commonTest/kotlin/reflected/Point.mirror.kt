//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.recktangle

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

object PointMirror : MirrorClass<Point>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Point> get() = Point::class as KClass<Point>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "com.lightningkite.recktangle"
    override val localName: String get() = "Point"
    override val implements: Array<MirrorClass<*>> get() = arrayOf(AnyMirror)
    override val companion: Any? get() = Point.Companion
    
    val fieldX: Field<Point,kotlin.Float> = Field(
        owner = this,
        index = 0,
        name = "x",
        type = FloatMirror,
        optional = true,
        get = { it.x },
        annotations = listOf<Annotation>()
    )
    
    val fieldY: Field<Point,kotlin.Float> = Field(
        owner = this,
        index = 1,
        name = "y",
        type = FloatMirror,
        optional = true,
        get = { it.y },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<Point, *>> = arrayOf(fieldX, fieldY)
    
    override fun deserialize(decoder: Decoder): Point {
        var xSet = false
        var fieldX: kotlin.Float? = null
        var ySet = false
        var fieldY: kotlin.Float? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldX = decoderStructure.decodeFloatElement(this, 0)
                    xSet = true
                    fieldY = decoderStructure.decodeFloatElement(this, 1)
                    ySet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldX = decoderStructure.decodeFloatElement(this, 0)
                    xSet = true
                }
                1 -> {
                    fieldY = decoderStructure.decodeFloatElement(this, 1)
                    ySet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!xSet) {
            fieldX = Point(
            ).x
        }
        if(!ySet) {
            fieldY = Point(
                x = fieldX as kotlin.Float
            ).y
        }
        return Point(
            x = fieldX as kotlin.Float,
            y = fieldY as kotlin.Float
        )
    }
    
    override fun serialize(encoder: Encoder, obj: Point) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeFloatElement(this, 0, obj.x)
        encoderStructure.encodeFloatElement(this, 1, obj.y)
        encoderStructure.endStructure(this)
    }
}
