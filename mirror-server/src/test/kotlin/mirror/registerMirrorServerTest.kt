package com.lightningkite.mirror.server

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerMirrorServerTest() = MirrorRegistry.register(
        com.lightningkite.mirror.server.test.PingRequestMirror,
        com.lightningkite.mirror.server.test.ThrowExceptionRequestMirror
)