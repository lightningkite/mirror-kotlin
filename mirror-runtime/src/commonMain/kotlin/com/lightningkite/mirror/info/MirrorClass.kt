package com.lightningkite.mirror.info

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerialKind
import kotlinx.serialization.StructureKind
import kotlin.reflect.KClass

abstract class MirrorClass<Type : Any> : MirrorType<Type> {
    override val base: MirrorClass<Type> get() = this
    override val descriptor: SerialDescriptor get() = this
    override val kind: SerialKind get() = StructureKind.CLASS

    open val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    open val modifiers: Array<Modifier> get() = arrayOf()
    open val owningClass: KClass<*>? get() = null
    open val companion: Any? get() = null
    open val annotations: List<Annotation> get() = listOf()
    open val enumValues: Array<Type>? get() = null
    open val implements: Array<MirrorClass<*>> get() = arrayOf()

    abstract val kClass: KClass<Type>
    abstract val packageName: String
    abstract val localName: String
    override val name: String get() = packageName + "." + localName

    val nullable by lazy { NullableMirrorType(this) }

    //Insert fields as `fieldX`
    abstract val fields: Array<Field<Type, *>>

    val fieldsIndex: Map<String, Int> by lazy { fields.withIndex().associate { it.value.name to it.index } }
    override val elementsCount: Int get() = fields.size
    override fun getElementIndex(name: String): Int = fieldsIndex[name] ?: CompositeDecoder.UNKNOWN_NAME
    override fun getElementName(index: Int): String = fields[index].name
    override fun getElementAnnotations(index: Int): List<Annotation> = fields[index].annotations
    override fun getElementDescriptor(index: Int): SerialDescriptor = fields[index].type
    override fun getEntityAnnotations(): List<Annotation> = annotations
    override fun isElementOptional(index: Int): Boolean = fields[index].optional

    data class Field<Owner, Value>(
            val owner: MirrorClass<*>,
            val name: String,
            val type: MirrorType<Value>,
            val optional: Boolean = false,
            val get: (Owner) -> Value,
            val set: ((Owner, Value) -> Unit)? = null,
            val annotations: List<Annotation> = listOf()
    )

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface,
        Inline,
        Object
    }
}