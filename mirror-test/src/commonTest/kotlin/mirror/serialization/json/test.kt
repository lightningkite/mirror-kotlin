package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.info.AnnotationInfo
import com.lightningkite.mirror.info.ClassInfo
import com.lightningkite.mirror.info.FieldInfo
import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

class TestType<T : Comparable<T>>(val wrapped: T)


@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
object TestTypeClassInfo : ClassInfo<TestType<*>> {

    override val kClass: KClass<TestType<*>> = TestType::class
    override val modifiers: List<ClassInfo.Modifier> = listOf(ClassInfo.Modifier.Data)

    override val implements: List<Type<*>> = listOf()

    override val packageName: String = "com.lightningkite.test"
    override val owner: KClass<*>? = null
    override val ownerName: String? = null

    override val name: String = "TestType"
    override val annotations: List<AnnotationInfo> = listOf()
    override val enumValues: List<TestType<*>>? = null

    object Fields {
        val wrapped = FieldInfo<TestType<*>, Comparable<Comparable<*>>>(
                TestTypeClassInfo,
                "wrapped",
                Type<Comparable<Comparable<*>>>(Comparable::class as KClass<Comparable<Comparable<*>>>, listOf(), false),
                false,
                { it.wrapped as Comparable<Comparable<*>> },
                listOf()
        )
    }

    override val fields: List<FieldInfo<TestType<*>, *>> = listOf(Fields.wrapped)

    override fun construct(map: Map<String, Any?>): TestType<Comparable<Comparable<*>>> {

        //Gather variables
        val wrapped: Comparable<Comparable<*>> = map["wrapped"] as Comparable<Comparable<*>>
        //Handle the optionals

        //Finally do the call
        return TestType<Comparable<Comparable<*>>>(
                wrapped = wrapped
        )
    }

}

