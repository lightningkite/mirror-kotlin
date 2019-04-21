package com.lightningkite.mirror

//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerTest() = MirrorRegistry.register(
    com.lightningkite.mirror.test.TreeMirror,
    com.lightningkite.mirror.test.IntDataMirror,
    com.lightningkite.mirror.test.AttitudeMirror,
    com.lightningkite.mirror.test.ZooMirror,
    com.lightningkite.mirror.info.TestAnotherInterfaceMirror,
    com.lightningkite.mirror.info.TestChildMirror,
    com.lightningkite.mirror.info.TestInterfaceMirror,
    com.lightningkite.mirror.info.TestParentMirror,
    com.lightningkite.lokalize.time.TimeStampMirror,
    com.lightningkite.mirror.test.TestEnumMirror,
    com.lightningkite.mirror.test.PostMirror,
    com.lightningkite.recktangle.PointMirror
)