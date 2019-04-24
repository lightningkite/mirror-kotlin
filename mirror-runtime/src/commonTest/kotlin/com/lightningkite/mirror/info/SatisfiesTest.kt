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

    @Test fun basic(){
        val toSatisfy = SatisfiesExampleMirror(IntMirror)
        val with = SatisfiesExampleFirstMirror.minimal
        val result = with.satisfies(toSatisfy)
        assertTrue { result == SatisfiesExampleFirstMirror(IntMirror) }
    }

    @Test fun number(){
        val toSatisfy = NumberMirror
        val with = IntMirror
        val result = with.satisfies(toSatisfy)
        assertTrue { result == IntMirror }
    }

    fun printOptionsFor(type: MirrorType<*>){
        val options = MirrorRegistry.allSatisfying(type)
        println("Options for $type:")
        for(opt in options){
            println("    $opt")
        }
        println()
    }

    @Test fun getAll(){
        printOptionsFor(SatisfiesExampleMirror(IntMirror))
        printOptionsFor(SatisfiesExampleMirror(StringMirror))
        printOptionsFor(SatisfiesExampleMirror(SatisfiesExampleMirror(IntMirror)))
        printOptionsFor(NumberMirror)
        assertTrue { MirrorRegistry.allSatisfying(NumberMirror).contains(IntMirror) }
        printOptionsFor(AnyMirror)
    }
}