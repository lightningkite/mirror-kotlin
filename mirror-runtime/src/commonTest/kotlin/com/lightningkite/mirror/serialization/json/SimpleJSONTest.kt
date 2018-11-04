package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlin.test.Test

class SimpleJSONTest {

    fun <T : Any> test(value: T, type: Type<T>) {
        val result = JsonSerializer.write(value, type)
        println(result)
        val back = JsonSerializer.read(result, type)
    }

    @Test
    fun test() {
        test(listOf(1, 2, 3, 4), Int::class.type.list)
        test(mapOf(
                "a" to 1,
                "b" to 2,
                "c" to 3
        ), Int::class.type.stringMap)
        test(mapOf(
                "a" to listOf(1, 8),
                "b" to listOf(2, 8),
                "c" to listOf(3, 8)
        ), Int::class.type.list.stringMap)
    }

    data class TestType(val a: Int, val b: String, val c: TestType?)

    object TestTypeClassInfo : ClassInfo<TestType> {
        override val kClass: KClass<TestType> get() = TestType::class
        override val implements: List<Type<*>> get() = listOf()
        override val packageName: String get() = "com.lightningkite.mirror.serialization.json"
        override val owner: KClass<*>? get() = SimpleJSONTest::class
        override val ownerName: String? get() = "SimpleJSONTest"
        override val name: String get() = "TestType"
        override val modifiers: List<ClassInfo.Modifier> get() = listOf()
        override val annotations: List<AnnotationInfo> get() = listOf()
        override val enumValues: List<TestType>? get() = null

        object Fields {
            val a = SerializedFieldInfo(TestTypeClassInfo, "a", Int::class.type, false, { it.a }, listOf())
            val b = SerializedFieldInfo(TestTypeClassInfo, "b", String::class.type, false, { it.b }, listOf())
            val c = SerializedFieldInfo(TestTypeClassInfo, "c", TestType::class.typeNullable, false, { it.c }, listOf())
        }

        override val fields: List<SerializedFieldInfo<TestType, *>> = listOf(Fields.a, Fields.b, Fields.c)

        override fun construct(map: Map<String, Any?>): TestType {
            return TestType(a = map["a"] as Int, b = map["b"] as String, c = map["c"] as TestType?)
        }

    }

    @Test
    fun reflective() {
        ClassInfo.register(TestTypeClassInfo)

        test(TestType(42, "hello", null), TestType::class.type)
        test(listOf(TestType(42, "hello", null), TestType(2, "dx", TestType(3, "da", null))), TestType::class.type.list)
    }

    @Test
    fun polymorphic() {
        ClassInfo.register(TestTypeClassInfo)

        test(TestType(42, "hello", null), Any::class.type)
        test(listOf(TestType(42, "hello", null)), Any::class.type)
        test(8, Any::class.type)
        test("hello", Any::class.type)
    }
}