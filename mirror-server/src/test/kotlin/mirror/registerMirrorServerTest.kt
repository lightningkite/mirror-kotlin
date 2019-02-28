package com.lightningkite.mirror.server

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerMirrorServerTest() = MirrorRegistry.register(
        com.lightningkite.mirror.request.RemoteExceptionDataMirror,
        com.lightningkite.mirror.server.test.PingRequestMirror,
        com.lightningkite.mirror.request.RequestMirror.minimal,
        com.lightningkite.mirror.server.test.ThrowExceptionRequestMirror
)