//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

object PostMirror : MirrorClass<Post>() {
    override val empty: Post get() = Post(
        id = null,
        userId = 0,
        title = "",
        body = ""
    )
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Post> get() = Post::class as KClass<Post>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Data)
    override val packageName: String get() = "com.lightningkite.mirror.test"
    override val localName: String get() = "Post"
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    
    val fieldId: Field<Post,Long?> = Field(
        owner = this,
        index = 0,
        name = "id",
        type = LongMirror.nullable,
        optional = true,
        get = { it.id },
        set = { it, value -> it.id = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldUserId: Field<Post,Long> = Field(
        owner = this,
        index = 1,
        name = "userId",
        type = LongMirror,
        optional = true,
        get = { it.userId },
        set = { it, value -> it.userId = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldTitle: Field<Post,String> = Field(
        owner = this,
        index = 2,
        name = "title",
        type = StringMirror,
        optional = true,
        get = { it.title },
        set = { it, value -> it.title = value },
        annotations = listOf<Annotation>()
    )
    
    val fieldBody: Field<Post,String> = Field(
        owner = this,
        index = 3,
        name = "body",
        type = StringMirror,
        optional = true,
        get = { it.body },
        set = { it, value -> it.body = value },
        annotations = listOf<Annotation>()
    )
    
    override val fields: Array<Field<Post, *>> = arrayOf(fieldId, fieldUserId, fieldTitle, fieldBody)
    
    override fun deserialize(decoder: Decoder): Post {
        var idSet = false
        var fieldId: Long? = null
        var userIdSet = false
        var fieldUserId: Long? = null
        var titleSet = false
        var fieldTitle: String? = null
        var bodySet = false
        var fieldBody: String? = null
        val decoderStructure = decoder.beginStructure(this)
        loop@ while (true) {
            when (decoderStructure.decodeElementIndex(this)) {
                CompositeDecoder.READ_ALL -> {
                    fieldId = decoderStructure.decodeSerializableElement(this, 0, LongMirror.nullable)
                    idSet = true
                    fieldUserId = decoderStructure.decodeLongElement(this, 1)
                    userIdSet = true
                    fieldTitle = decoderStructure.decodeStringElement(this, 2)
                    titleSet = true
                    fieldBody = decoderStructure.decodeStringElement(this, 3)
                    bodySet = true
                    break@loop
                }
                CompositeDecoder.READ_DONE -> break@loop
                0 -> {
                    fieldId = decoderStructure.decodeSerializableElement(this, 0, LongMirror.nullable)
                    idSet = true
                }
                1 -> {
                    fieldUserId = decoderStructure.decodeLongElement(this, 1)
                    userIdSet = true
                }
                2 -> {
                    fieldTitle = decoderStructure.decodeStringElement(this, 2)
                    titleSet = true
                }
                3 -> {
                    fieldBody = decoderStructure.decodeStringElement(this, 3)
                    bodySet = true
                }
                else -> {}
            }
        }
        decoderStructure.endStructure(this)
        if(!idSet) {
            fieldId = null
        }
        if(!userIdSet) {
            fieldUserId = 0
        }
        if(!titleSet) {
            fieldTitle = ""
        }
        if(!bodySet) {
            fieldBody = ""
        }
        return Post(
            id = fieldId as Long?,
            userId = fieldUserId as Long,
            title = fieldTitle as String,
            body = fieldBody as String
        )
    }
    
    override fun serialize(encoder: Encoder, obj: Post) {
        val encoderStructure = encoder.beginStructure(this)
        encoderStructure.encodeSerializableElement(this, 0, LongMirror.nullable, obj.id)
        encoderStructure.encodeLongElement(this, 1, obj.userId)
        encoderStructure.encodeStringElement(this, 2, obj.title)
        encoderStructure.encodeStringElement(this, 3, obj.body)
        encoderStructure.endStructure(this)
    }
}
