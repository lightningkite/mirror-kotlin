package com.lightningkite.mirror.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor

interface MirrorType<Type> : KSerializer<Type>, SerialDescriptor {
    val base: MirrorClass<*>
}