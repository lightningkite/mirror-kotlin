//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.info

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass

@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST", "USELESS_CAST")
object TestParentClassInfo: ClassInfo<TestParent> {

   override val kClass: KClass<TestParent> = TestParent::class
   override val modifiers: List<ClassInfo.Modifier> = listOf(ClassInfo.Modifier.Open)

   override val implements: List<Type<*>> = listOf(Type<TestInterface>(TestInterface::class, listOf(), false))

   override val packageName: String = "com.lightningkite.mirror.info"
   override val owner: KClass<*>? = null
   override val ownerName: String? = null

   override val name: String = "TestParent"
   override val annotations: List<AnnotationInfo> = listOf()
   override val enumValues: List<TestParent>? = null

   

   override val fields:List<FieldInfo<TestParent, *>> = listOf()

   override fun construct(map: Map<String, Any?>): TestParent {
       //Gather variables
       
           //Handle the optionals
       
       //Finally do the call
       return TestParent(
           
       )
   }

}