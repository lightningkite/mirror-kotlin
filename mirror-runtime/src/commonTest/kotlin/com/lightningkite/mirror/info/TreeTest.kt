//package com.lightningkite.mirror.info
//
//import com.lightningkite.mirror.TestRegistry
//import kotlin.test.Test
//
//class TreeTest {
//    val registry = PrimitiveClassInfoRegistry + TestRegistry
//
//    @Test fun printTree(){
//        val tree = registry[TestChild::class]!!.implementsTree(registry)
//        println(tree)
//    }
//
//    @Test fun testParent(){
//        val tree = registry[TestChild::class]!!.implementsTree(registry).pathTo(TestInterface::class)
//        println(tree)
//    }
//}