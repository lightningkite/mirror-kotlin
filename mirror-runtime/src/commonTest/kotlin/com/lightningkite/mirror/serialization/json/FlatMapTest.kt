//package com.lightningkite.mirror.serialization.json
//
//import com.lightningkite.kommon.bytes.toStringHex
//import com.lightningkite.lokalize.time.TimeStamp
//import com.lightningkite.lokalize.time.TimeStampMirror
//import com.lightningkite.mirror.flatmap.FlatMapFormat
//import com.lightningkite.mirror.info.*
//import com.lightningkite.mirror.registerTest
//import com.lightningkite.mirror.test.Post
//import com.lightningkite.mirror.test.PostMirror
//import com.lightningkite.mirror.test.TestEnum
//import com.lightningkite.mirror.test.TestEnumMirror
//import com.lightningkite.recktangle.Point
//import com.lightningkite.recktangle.PointMirror
//import kotlinx.serialization.json.Json
//import kotlin.test.Test
//
//class FlatMapTest {
//
//    init{
//        registerTest()
//    }
//
//    fun <T> test(value: T, type: MirrorType<T>): T {
//        val result = FlatMapFormat.toMap(type, value)
//        val schema = FlatMapFormat.columns(type)
//        println("SCHEMA: $schema")
//        println("VALUE: $result")
//        val back = FlatMapFormat.fromMap(type, result)
//        return back
//    }
//
//    @Test
//    fun listString() {
//        val map = FlatMapFormat.toMap(IntMirror.list, listOf(1, 2, 3, 4))
//        println((map[""] as ByteArray).toStringHex())
//        val back = FlatMapFormat.fromMap(IntMirror.list, map)
//        println(back)
//    }
//
//    @Test
//    fun basicsTest() {
//        test(listOf(1, 2, 3, 4), IntMirror.list)
//        test(mapOf(
//                "a" to 1,
//                "b" to 2,
//                "c" to 3
//        ), StringMirror mapTo IntMirror)
//        test(mapOf(
//                "a" to listOf(1, 8),
//                "b" to listOf(2, 8),
//                "c" to listOf(3, 8)
//        ), StringMirror mapTo IntMirror.list)
//    }
//
//    @Test
//    fun nullables() {
//        test<String?>(null, StringMirror.nullable)
//        test<String?>("value", StringMirror.nullable)
//        test(listOf(null, "Has String", null, "another"), StringMirror.nullable.list)
//    }
//
//    @Test
//    fun reflective() {
//        test(Post(0, 42, "hello"), PostMirror)
//    }
//
//    @Test
//    fun polymorphic() {
//        test(Post(0, 42, "hello"), AnyMirror)
//        test(listOf(Post(0, 42, "hello")), AnyMirror)
//        test(8, AnyMirror)
//        test("hello", AnyMirror)
//    }
//
//    @Test
//    fun types(){
//        test(listOf<Any>(
//                Unit,
//                true,
//                false,
//                'c',
//                "string",
//                1.toByte(),
//                1.toShort(),
//                1,
//                1L,
//                1f,
//                1.0
//        ), AnyMirror)
//    }
//
//    @Test
//    fun enumTest(){
//        test(TestEnum.ValueA, TestEnumMirror)
//        test(TestEnum.ValueB, TestEnumMirror)
//        test(TestEnum.ValueC, TestEnumMirror)
//    }
//
//    @Test
//    fun reflectiveData(){
//        test(PostMirror.fieldId, MirrorClassFieldMirror)
//        test(TestEnumMirror, MirrorClassMirror)
//    }
//
//    @Test
//    fun externalClass() {
//        test(Point(1f, 2f), PointMirror)
//    }
//
//    @Test
//    fun inlinedClass() {
//        test(TimeStamp(41782934718L), TimeStampMirror)
//    }
//}
