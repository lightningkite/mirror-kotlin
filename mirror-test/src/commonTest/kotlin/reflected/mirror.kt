package com.lightningkite.mirror

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun configureMirror() {
    ClassInfo.register(com.lightningkite.recktangle.PointClassInfo)
}