//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.info

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST", "USELESS_CAST")
object TestAnotherInterfaceClassInfo: ClassInfo<TestAnotherInterface> {

   override val kClass: KClass<TestAnotherInterface> = TestAnotherInterface::class
   override val modifiers: List<ClassInfo.Modifier> = listOf(ClassInfo.Modifier.Interface)

   override val implements: List<Type<*>> = listOf()

   override val packageName: String = "com.lightningkite.mirror.info"
   override val owner: KClass<*>? = null
   override val ownerName: String? = null

   override val name: String = "TestAnotherInterface"
   override val annotations: List<AnnotationInfo> = listOf()
   override val enumValues: List<TestAnotherInterface>? = null

   

   override val fields:List<FieldInfo<TestAnotherInterface, *>> = listOf()

   override fun construct(map: Map<String, Any?>): TestAnotherInterface = throw NotImplementedError()

}