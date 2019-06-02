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

    override val empty: Map<K, V>
        get() = mapOf()

    companion object: MirrorClassCompanion {
        val KMirrorMinimal = TypeArgumentMirrorType("K", Variance.INVARIANT, AnyMirror.nullable)
        val VMirrorMinimal = TypeArgumentMirrorType("V", Variance.OUT, AnyMirror.nullable)
        override val minimal = MapMirror(KMirrorMinimal, VMirrorMinimal)
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = MapMirror(typeArguments[0], typeArguments[1])
        fun make(KMirror: MirrorType<*>?, VMirror: MirrorType<*>?) = MapMirror(KMirror ?: KMirrorMinimal, VMirror ?: VMirrorMinimal)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(KMirror, VMirror)
    override val kClass: KClass<Map<K, V>> get() = Map::class as KClass<Map<K, V>>
    override val packageName: String get() = "kotlin.collections"
    override val localName: String get() = "Map"
    override val fields: Array<Field<Map<K, V>, *>> get() = arrayOf()
    override val companion: Any? get() = null
}