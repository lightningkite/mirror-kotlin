package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
val KClass<out Request<*>>.throwsTypes: List<String>?
    get() {
        return MirrorRegistry[this]!!.annotations
                .mapNotNull { it as? MirrorAnnotation }
                .find { it.annotationType == ThrowsTypes::class }?.asMap()?.values?.mapNotNull { it as? String }
    }

val Request<*>.throwsTypes: List<String>? get() = this::class.throwsTypes

