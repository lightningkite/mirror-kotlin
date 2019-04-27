package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.map
import kotlin.reflect.KClass

data class MapMirror<K, V>(
        val KMirror: MirrorType<K>,
        val VMirror: MirrorType<V>
) : MirrorClass<Map<K, V>>(),
        KSerializer<Map<K, V>> by (KMirror to VMirror).map,
        SerialDescriptor by (KMirror to VMirror).map.descriptor {
    override val mirrorClassCompanion: MirrorClassCompanion?
        get() = Companion

    companion object: MirrorClassCompanion {
        override val minimal = MapMirror(TypeArgumentMirrorType("K", Variance.INVARIANT, AnyMirror.nullable), TypeArgumentMirrorType("V", Variance.OUT, AnyMirror.nullable))
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = MapMirror(typeArguments[0], typeArguments[1])
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(KMirror, VMirror)
    override val kClass: KClass<Map<K, V>> get() = Map::class as KClass<Map<K, V>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "Map"
    override val fields: Array<Field<Map<K, V>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}