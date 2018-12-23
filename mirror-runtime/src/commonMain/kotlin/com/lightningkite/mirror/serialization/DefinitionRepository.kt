package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.isFrozen
import com.lightningkite.mirror.info.Type
import kotlin.reflect.KClass

interface DefinitionRepository<DEFINITION: Any> {
    val registry: SerializationRegistry
    val arbitraryDefines: MutableList<Generator<DEFINITION>>
    val kClassDefines: MutableMap<KClass<*>, (Type<*>) -> DEFINITION?>
    val definitions: MutableMap<Type<*>, DEFINITION>

    @Suppress("UNCHECKED_CAST")
    fun initializeDefinitions(){
        registry.defineConfigurators.forEach { it.value.invoke(this as DefinitionRepository<Any>) }
    }

    fun <T> addDefinition(type: Type<T>, action: DEFINITION) {
        @Suppress("UNCHECKED_CAST")
        definitions[type] = action
    }

    fun <T : Any> addDefinition(kClass: KClass<T>, action: (Type<*>) -> (DEFINITION)?) {
        @Suppress("UNCHECKED_CAST")
        kClassDefines[kClass] = action
    }

    fun addDefinition(generator: Generator<DEFINITION>) {
        arbitraryDefines.removeAll { it.description == generator.description }
        arbitraryDefines.addSorted(generator) { a, b -> a.priority > b.priority }
    }


    fun <T> definition(type: Type<T>): DEFINITION = rawDefine(type)

    fun rawDefine(type: Type<*>): DEFINITION =
            if(isFrozen){
                definitions.getOrElse(type) {
                    kClassDefines[type.kClass]?.invoke(type) ?: arbitraryDefines.asSequence()
                            .mapNotNull { it.generateDefine(type) }
                            .firstOrNull() ?: throw SerializationException("No definition generated for $type!")
                }
            } else {
                definitions.getOrPut(type) {
                    kClassDefines[type.kClass]?.invoke(type) ?: arbitraryDefines.asSequence()
                            .mapNotNull { it.generateDefine(type) }
                            .firstOrNull() ?: throw SerializationException("No definition generated for $type!")
                }
            }

    interface Generator<OUT> : Comparable<Generator<OUT>> {
        val description: String
        val priority: Float
        override fun compareTo(other: Generator<OUT>): Int = other.priority.compareTo(priority)
        fun generateDefine(type: Type<*>): OUT?
    }
}