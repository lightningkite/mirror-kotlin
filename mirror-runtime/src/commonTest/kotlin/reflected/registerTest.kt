package com.lightningkite.mirror

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerTest() = MirrorClassMirror.register(
    com.lightningkite.mirror.serialization.json.TreeMirror,
    com.lightningkite.mirror.serialization.json.IntDataMirror,
    com.lightningkite.mirror.serialization.json.AttitudeMirror,
    com.lightningkite.mirror.serialization.json.ZooMirror,
    com.lightningkite.mirror.info.TestAnotherInterfaceMirror,
    com.lightningkite.mirror.info.TestChildMirror,
    com.lightningkite.mirror.info.TestInterfaceMirror,
    com.lightningkite.mirror.info.TestParentMirror,
    com.lightningkite.mirror.serialization.json.SimpleJSONTestTestEnumMirror,
    com.lightningkite.mirror.serialization.json.SimpleJSONTestPostMirror,
        com.lightningkite.recktangle.PointMirror,
        com.lightningkite.lokalize.time.TimeStampMirror
)
