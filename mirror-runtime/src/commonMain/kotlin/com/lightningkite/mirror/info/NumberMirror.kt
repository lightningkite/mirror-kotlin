package com.lightningkite.mirror.info

import kotlin.reflect.KClass

object NumberMirror : PolymorphicMirror<Number>() {
    override val empty: Number
        get() = 0
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Number> get() = Number::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Number"
    override val companion: Any? get() = null
}