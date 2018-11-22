package com.lightningkite.mirror

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@SharedImmutable
val TestRegistry = ClassInfoRegistry(
    com.lightningkite.recktangle.PointClassInfo,
    com.lightningkite.lokalize.TimeStampClassInfo
)