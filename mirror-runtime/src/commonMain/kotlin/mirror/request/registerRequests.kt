package com.lightningkite.mirror.request

//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerRequests() = MirrorRegistry.register(
    com.lightningkite.mirror.request.RemoteResultMirror.minimal,
    com.lightningkite.mirror.request.RemoteExceptionDataMirror,
    com.lightningkite.mirror.request.RequestMirror.minimal
)