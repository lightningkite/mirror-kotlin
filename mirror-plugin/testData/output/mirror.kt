package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun configureMirror() {
    ClassInfo.register(com.lightningkite.recktanglePointClassInfo)
    ClassInfo.register(com.lightningkite.recktangleRectangleClassInfo)
    ClassInfo.register(com.lightningkite.kotlinx.persistenceChangeEventClassInfo)
}