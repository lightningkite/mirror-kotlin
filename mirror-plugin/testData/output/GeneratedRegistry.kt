package com.lightningkite.mirror

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

fun GeneratedRegistry() = MirrorRegistry.register(
    test.AnnotatedClassMirror,
    test.BoxMirror,
        test.TestInterfaceMirror,
    test.DefaultsTestMirror
)