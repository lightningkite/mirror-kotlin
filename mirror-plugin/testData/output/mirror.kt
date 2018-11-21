package com.lightningkite.mirror.test

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@SharedImmutable
val configureMirror = ClassInfoRegistry(
    ClassInfo.register(com.lightningkite.rekwest.server.UserQueryClassInfo)
)