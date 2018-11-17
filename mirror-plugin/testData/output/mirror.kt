package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun configureMirror(){
    ClassInfo.register(com.lightningkite.rekwest.server.UserQueryClassInfo)
}