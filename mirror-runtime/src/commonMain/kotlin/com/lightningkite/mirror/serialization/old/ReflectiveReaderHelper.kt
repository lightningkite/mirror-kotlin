//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.kotlinx.reflection.*
//import kotlin.reflect.KClass
//
//class ReflectiveReaderHelper<IN>(
//        val type: KClass<*>,
//        val kx: ClassInfo<*>,
//        val usingConstructor: KxFunction<*>,
//        val args: Map<String, KxArgument>,
//        val vars: Map<String, KxVariable<*, *>>,
//        val readers: Map<String, TypeReader<IN>>
//) {
//    fun instanceBuilder() = InstanceBuilder(this)
//
//    class InstanceBuilder<IN>(val helper: ReflectiveReaderHelper<IN>) {
//        val arguments = HashMap<String, Any?>()
//        val toPlace = ArrayList<Pair<KxVariable<*, *>, Any?>>()
//
//        inline fun place(name: String, input: IN, skipField: () -> Unit) {
//            helper.args[name]?.let { a ->
//                val value = helper.readers[a.name]!!.invoke(input, a.type)
//                arguments[name] = value
//            } ?: helper.vars[name]?.let { v ->
//                val value = helper.readers[v.name]!!.invoke(input, v.type)
//                toPlace.add(v to value)
//            } ?: run {
//                skipField()
//            }
//        }
//
//        inline fun placeManual(name: String, value: Any?) {
//            helper.args[name]?.let { a ->
//                arguments[name] = value
//            } ?: helper.vars[name]?.let { v ->
//                toPlace.add(v to value)
//            }
//        }
//
//        inline fun build(): Any {
//            val instance = helper.usingConstructor.callGiven(arguments)!!
//            instance.setUntyped(toPlace)
//            return instance
//        }
//    }
//
//    companion object {
//        fun <IN> tryInit(type: KClass<*>, forReaderRepository: TypeReaderRepository<IN>): ReflectiveReaderHelper<IN>? {
//
//            val kx = type.kxReflect
//            val constructor = kx.constructors.firstOrNull() ?: return null
//            val args = constructor.arguments.associate { it.name to it }
//            val vars = kx.variables
//            val readers = vars.values.associate {
//                it.name to forReaderRepository.reader(it.type.base.kClass)
//            } + args.values.associate {
//                it.name to forReaderRepository.reader(it.type.base.kClass)
//            }
//
//            return ReflectiveReaderHelper(
//                    type = type,
//                    usingConstructor = constructor,
//                    kx = kx,
//                    args = args,
//                    vars = vars,
//                    readers = readers
//            )
//        }
//    }
//}