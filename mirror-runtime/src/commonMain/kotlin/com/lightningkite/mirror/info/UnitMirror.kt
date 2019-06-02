package com.lightningkite.mirror.info

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialKind
import kotlin.reflect.KClass

object UnitMirror : MirrorClass<Unit>() {
    override val empty: Unit get() = Unit
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Unit> get() = Unit::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Unit"
    override val fields: Array<Field<Unit, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.UNIT
    override val companion: Any? get() = null
    override fun deserialize(decoder: Decoder): Unit = decoder.decodeUnit()
    override fun serialize(encoder: Encoder, obj: Unit) = encoder.encodeUnit()
}