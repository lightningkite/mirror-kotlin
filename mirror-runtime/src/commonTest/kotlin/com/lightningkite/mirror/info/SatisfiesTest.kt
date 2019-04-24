package com.lightningkite.mirror.info

import com.lightningkite.mirror.registerTest
import com.lightningkite.mirror.test.SatisfiesExample
import com.lightningkite.mirror.test.SatisfiesExampleFirstMirror
import com.lightningkite.mirror.test.SatisfiesExampleMirror
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class SatisfiesTest {

    init {
        registerTest()
    }

    @Test fun test(){
        val toSatisfy = SatisfiesExampleMirror(IntMirror)
        val with = SatisfiesExampleFirstMirror.minimal
        val result = with.satisfies(toSatisfy)
        assertTrue { result == SatisfiesExampleFirstMirror(IntMirror) }
    }
}