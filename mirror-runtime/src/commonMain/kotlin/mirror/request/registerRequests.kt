package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerRequests() = MirrorRegistry.register(
    com.lightningkite.mirror.request.RemoteResultMirror.minimal,
    com.lightningkite.mirror.request.RemoteExceptionDataMirror,
    com.lightningkite.mirror.request.RequestMirror.minimal
)