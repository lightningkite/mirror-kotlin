package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun registerKotlin() = MirrorRegistry.register(
        mirror.kotlin.TripleMirror.minimal,
        mirror.kotlin.PairMirror.minimal
)