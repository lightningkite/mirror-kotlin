//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.info


import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST", "USELESS_CAST")
object TestInterfaceClassInfo: ClassInfo<TestInterface> {

   override val kClass: KClass<TestInterface> = TestInterface::class
   override val modifiers: List<ClassInfo.Modifier> = listOf(ClassInfo.Modifier.Interface)

   override val implements: List<Type<*>> = listOf()

   override val packageName: String = "com.lightningkite.mirror.info"
   override val owner: KClass<*>? = null
   override val ownerName: String? = null

   override val name: String = "TestInterface"
   override val annotations: List<AnnotationInfo> = listOf()
   override val enumValues: List<TestInterface>? = null

   object Fields {
       
   }

   override val fields:List<FieldInfo<TestInterface, *>> = listOf()

   override fun construct(map: Map<String, Any?>): TestInterface = throw NotImplementedError()

}