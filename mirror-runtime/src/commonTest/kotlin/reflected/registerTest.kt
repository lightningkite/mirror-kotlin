package com.lightningkite.mirror

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerTest() = MirrorRegistry.register(
    com.lightningkite.lokalize.time.TimeStampMirror,
    com.lightningkite.mirror.serialization.json.SimpleJSONTestTestEnumMirror,
    com.lightningkite.mirror.serialization.json.SimpleJSONTestPostMirror,
    com.lightningkite.recktangle.PointMirror
)