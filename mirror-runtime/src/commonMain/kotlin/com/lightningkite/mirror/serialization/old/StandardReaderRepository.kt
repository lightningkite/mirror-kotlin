//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.Type
//import com.lightningkite.mirror.serialization.json.KlaxonException
//import kotlin.reflect.KClass
//
//interface StandardReaderRepository<IN> : TypeReaderRepository<IN> {
//    val readerGenerators: MutableList<Pair<Float, TypeReaderGenerator<IN>>>
//    val readers: MutableMap<KClass<*>, TypeReader<IN>>
//
//    override fun reader(type: KClass<*>): TypeReader<IN> = readers.getOrPut(type) {
//        CommonSerialization.getDirectSubReader(this, type)
//                ?: readerGenerators.asSequence().mapNotNull {
//                    try {
//                        it.second.invoke(type)
//                    } catch (e: Exception) {
//                        println(e.stackTraceString())
//                        null
//                    }
//                }.firstOrNull()
//                ?: throw KlaxonException("No reader available for type $type")
//    }
//
//    fun <T : Any> setReader(forType: KClass<T>, reader: IN.(Type<*>) -> T?) = readers.put(forType, reader)
//
//    fun addReaderGenerator(priority: Float, readerGenerator: TypeReaderGenerator<IN>) {
//        readerGenerators.addSorted(priority to readerGenerator) { a, b -> a.first > b.first }
//    }
//
//    fun read(type: Type<*>, from: IN): Any? = reader(type.kClass).invoke(from, type)
//}