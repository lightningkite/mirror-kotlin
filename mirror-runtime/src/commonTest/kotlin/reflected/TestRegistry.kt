package com.lightningkite.mirror

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@SharedImmutable
val TestRegistry = ClassInfoRegistry(
        com.lightningkite.lokalize.time.TimeStampClassInfo,
    com.lightningkite.recktangle.PointClassInfo,
        com.lightningkite.mirror.serialization.json.SimpleJSONTestPostClassInfo,
        com.lightningkite.mirror.serialization.json.SimpleJSONTestTestEnumClassInfo,
    com.lightningkite.mirror.info.TestInterfaceClassInfo,
    com.lightningkite.mirror.info.TestParentClassInfo,
    com.lightningkite.mirror.info.TestAnotherInterfaceClassInfo,
        com.lightningkite.mirror.info.TestChildClassInfo
)
