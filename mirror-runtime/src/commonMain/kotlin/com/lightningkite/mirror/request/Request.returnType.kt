package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.MirrorClassMirror
import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.MirrorType
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
val KClass<out Request<*>>.returnType: MirrorType<*>
    get() = MirrorRegistry[this]!!.implements
            .find { it.name == "com.lightningkite.mirror.request.Request" }
            .let { if (it == null) throw IllegalArgumentException("This type (${this}) does not extend Request") else it }
            .typeParameters.first()

inline val <reified R : Request<*>> R.returnType: MirrorType<*>
    get() = this::class.returnType

