package com.lightningkite.mirror.info

import kotlin.reflect.KClass

data class ComparatorMirror<T>(
        val typeT: MirrorType<T>
) : PolymorphicMirror<Comparator<T>>() {
    companion object {
        val minimal = ListMirror(AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(typeT)
    override val kClass: KClass<Comparator<T>> get() = Comparator::class as KClass<Comparator<T>>
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Comparator"
    override val companion: Any? get() = null
}