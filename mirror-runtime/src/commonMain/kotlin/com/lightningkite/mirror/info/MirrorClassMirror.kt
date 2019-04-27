package com.lightningkite.mirror.info

import kotlinx.serialization.*
import kotlin.reflect.KClass

data class MirrorClassMirror<Type : Any>(val TypeMirror: MirrorType<Type>) : MirrorClass<MirrorClass<Type>>() {
    override val mirrorClassCompanion: MirrorClassCompanion?
        get() = Companion

    companion object: MirrorClassCompanion {
        @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.Index", "com.lightningkite.mirror.info.MirrorRegistry"))
        class Index(
                val byName: Map<String, MirrorClass<*>>,
                val byClass: Map<KClass<*>, MirrorClass<*>>
        )

        @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.index", "com.lightningkite.mirror.info.MirrorRegistry"))
        val index
            get() = MirrorRegistry.index

        @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.register(mirror)", "com.lightningkite.mirror.info.MirrorRegistry"))
        fun register(vararg mirror: MirrorClass<*>) = MirrorRegistry.register(*mirror)

        @Deprecated("Index has been moved to MirrorRegistry.", ReplaceWith("MirrorRegistry.retrieve(any)", "com.lightningkite.mirror.info.MirrorRegistry"))
        fun retrieve(any: Any): MirrorClass<*> = MirrorRegistry.retrieve(any)

        override val minimal = MirrorClassMirror(TypeArgumentMirrorType("Type", Variance.INVARIANT, AnyMirror))
        @Suppress("UNCHECKED_CAST")
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = MirrorClassMirror(typeArguments[0] as MirrorType<Any>)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TypeMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<MirrorClass<Type>>
        get() = MirrorClass::class as KClass<MirrorClass<Type>>
    override val packageName: String get() = "com.lightningkite.mirror.info"
    override val localName: String get() = "MirrorClass"
    override val fields: Array<Field<MirrorClass<Type>, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.STRING
    override val companion: Any? get() = null
    override fun deserialize(decoder: Decoder): MirrorClass<Type> {
        val typeName = decoder.decodeString()
        @Suppress("UNCHECKED_CAST")
        return MirrorRegistry[typeName] as? MirrorClass<Type>
                ?: throw SerializationException("Unknown type name '$typeName', did you register it?")
    }

    override fun serialize(encoder: Encoder, obj: MirrorClass<Type>) = encoder.encodeString(obj.name)
}

