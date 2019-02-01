package com.lightningkite.mirror

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun GeneratedRegistry() = MirrorClassMirror.register(
    test.AnnotatedClassMirror,
    test.BoxMirror,
    test.DefaultsTestMirror
)