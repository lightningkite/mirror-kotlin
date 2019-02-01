package com.lightningkite.mirror.info

import kotlin.reflect.KClass

interface MirrorAnnotation: Annotation {
    val annotationType: KClass<out Annotation>
    fun asMap(): Map<String, Any?>
}