package com.lightningkite.mirror.info

import com.lightningkite.mirror.registerTest
import com.lightningkite.mirror.test.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class IsATest {

    init {
        registerTest()
    }

    @Test fun test(){
        assertTrue { IntMirror isA AnyMirror }
        assertTrue { ListMirror(IntMirror) isA AnyMirror }
        assertTrue { SatisfiesExampleFirstMirror(IntMirror) isA SatisfiesExampleMirror(IntMirror) }
        assertTrue { SatisfiesExampleSecondMirror(IntMirror) isA SatisfiesExampleMirror(IntMirror) }
        assertTrue { SatisfiesExampleThirdMirror(IntMirror) isA SatisfiesExampleMirror(IntMirror) }
    }
}