//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.Type
//import com.lightningkite.mirror.serialization.json.KlaxonException
//import kotlin.reflect.KClass
//
//interface StandardWriterRepository<OUT, RESULT> : TypeWriterRepository<OUT, RESULT> {
//    val writerGenerators: MutableList<Pair<Float, TypeWriterGenerator<OUT, RESULT>>>
//
//    val writers: MutableMap<KClass<*>, TypeWriter<OUT, RESULT>>
//
//    override fun writer(type: KClass<*>): TypeWriter<OUT, RESULT> = writers.getOrPut(type) {
//        CommonSerialization.getDirectSubWriter(this, type)
//                ?: writerGenerators.asSequence().mapNotNull { it.second.invoke(type) }.firstOrNull()
//                ?: throw KlaxonException("No writer available for type $type")
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    fun <T : Any> setWriter(forType: KClass<T>, writer: OUT.(T?, Type<*>) -> RESULT) = writers.put(forType, writer as TypeWriter<OUT, RESULT>)
//
//    fun addWriterGenerator(priority: Float, writerGenerator: TypeWriterGenerator<OUT, RESULT>) {
//        writerGenerators.addSorted(priority to writerGenerator) { a, b -> a.first > b.first }
//    }
//
//    fun write(type: Type<*>, value: Any?, to: OUT): RESULT = writer(type.kClass).invoke(to, value, type)
//}