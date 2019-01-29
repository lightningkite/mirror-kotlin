package com.lightningkite.mirror.info

import kotlin.reflect.KClass

object AnyMirror : PolymorphicMirror<Any>() {
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Any> get() = Any::class
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Any"
    override val companion: Any? get() = null
}