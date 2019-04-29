package com.lightningkite.mirror.server

//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerMirrorServerTest() = MirrorRegistry.register(
    com.lightningkite.mirror.server.test.PingRequestMirror,
    com.lightningkite.mirror.server.test.ThrowExceptionRequestMirror
)