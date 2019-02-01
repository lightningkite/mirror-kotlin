package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.map
import kotlin.reflect.KClass

data class MapMirror<K, V>(
        val typeK: MirrorType<K>,
        val typeV: MirrorType<V>
) : MirrorClass<Map<K, V>>(),
        KSerializer<Map<K, V>> by (typeK to typeV).map,
        SerialDescriptor by (typeK to typeV).map.descriptor {
    companion object {
        val minimal = MapMirror(AnyMirror.nullable, AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(typeK, typeV)
    override val kClass: KClass<Map<K, V>> get() = Map::class as KClass<Map<K, V>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "Map"
    override val fields: Array<Field<Map<K, V>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}