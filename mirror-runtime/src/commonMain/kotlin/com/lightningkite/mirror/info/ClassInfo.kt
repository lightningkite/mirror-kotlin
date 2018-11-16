package com.lightningkite.mirror.info

import kotlin.reflect.KClass

interface ClassInfo<T : Any> {
    val kClass: KClass<T>
    val modifiers: List<Modifier>

    val implements: List<Type<*>>

    val packageName: String
    val ownerName: String?
    val owner: KClass<*>?
    val name: String

    val annotations: List<AnnotationInfo>
    val enumValues: List<T>?

    val fields: List<SerializedFieldInfo<T, *>>

    fun construct(map: Map<String, Any?>): T

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface
    }

    companion object {
        val map = HashMap<KClass<*>, ClassInfo<*>>()

        operator fun <T : Any> get(kClass: KClass<T>): ClassInfo<T> {
            @Suppress("UNCHECKED_CAST")
            return map.getOrPut(kClass) {
                EmptyClassInfo(kClass)
            } as ClassInfo<T>
        }

        fun register(
                reflection: ClassInfo<*>
        ) {
            map[reflection.kClass] = reflection
        }

        init {
            register(AnyClassInfo)
            register(UnitClassInfo)
            register(BooleanClassInfo)
            register(ByteClassInfo)
            register(ShortClassInfo)
            register(IntClassInfo)
            register(LongClassInfo)
            register(FloatClassInfo)
            register(DoubleClassInfo)
            register(NumberClassInfo)
            register(CharClassInfo)
            register(StringClassInfo)
            register(ListClassInfo)
            register(MapClassInfo)
        }
    }
}