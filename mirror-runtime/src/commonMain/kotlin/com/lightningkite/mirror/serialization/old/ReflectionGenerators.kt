//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.ClassInfo
//
//object ReflectionGenerators {
//
//
//    inline fun <OUT, RESULT, OBJCONTEXT> generateWriter(
//        forWriterRepository: TypeWriterRepository<OUT, RESULT>,
//        kx: ClassInfo<*>,
//        crossinline beginObject: OUT.() -> OBJCONTEXT,
//        crossinline writeKey: OBJCONTEXT.(String) -> Unit,
//        crossinline writeValue: OBJCONTEXT.(TypeWriter<OUT, RESULT>, Any?) -> Unit,
//        crossinline endObject: OBJCONTEXT.() -> RESULT
//    ): TypeWriter<OUT, RESULT> {
//        val vars = kx.fields
//        val writers = vars.values.associate { it.name to forWriterRepository.writer(it.type.kClass) }
//
//        return writer@{ typeInfo, value ->
//
//            val ctxt = beginObject()
//            for (v in vars) {
//                ctxt.writeKey(v.key)
//                val subValue = v.value.get.untyped.invoke(value)
//                ctxt.writeValue(writers[v.key]!!, subValue)
//            }
//            ctxt.endObject()
//        }
//    }
////
////    inline fun <IN> generateNoArgConstructorReader(
////            kx: ClassInfo<*>,
////            constructor: KxFunction<*>,
////            forReaderRepository: TypeReaderRepository<IN>,
////            crossinline readObject: IN.() -> Iterator<Pair<String, IN>>,
////            crossinline skipField: IN.() -> Unit
////    ): TypeReader<IN> {
////        val vars = kx.variables
////        val readers = vars.values.associate { it.name to forReaderRepository.reader(it.type.base.kClass) }
////
////        return reader@{ _ ->
////            val instance = constructor.call(listOf())!!
////            readObject().forEach { (key, newIn) ->
////                val v = vars[key]
////                if (v == null) {
////                    skipField()
////                } else {
////                    val value = readers[v.name]!!.invoke(newIn, v.type)
////                    v.set.untyped.invoke(instance, value)
////                }
////            }
////            instance
////        }
////    }
////
////    inline fun <IN> generateAnyConstructorReader(
////            kx: ClassInfo<*>,
////            constructor: KxFunction<*>,
////            forReaderRepository: TypeReaderRepository<IN>,
////            crossinline readObject: IN.() -> Iterator<Pair<String, IN>>,
////            crossinline skipField: IN.() -> Unit
////    ): TypeReader<IN> {
////        val args = constructor.arguments.associate { it.name to it }
////        val vars = kx.variables
////        val readers = vars.values.associate {
////            it.name to forReaderRepository.reader(it.type.base.kClass)
////        } + args.values.associate {
////            it.name to forReaderRepository.reader(it.type.base.kClass)
////        }
////
////        return reader@{ _ ->
////            val arguments = HashMap<String, Any?>()
////            val toPlace = ArrayList<Pair<KxVariable<*, *>, Any?>>()
////
////            //Get all of the data
////            readObject().forEach { (name, newIn) ->
////                args[name]?.let { a ->
////                    val value = readers[a.name]!!.invoke(newIn, a.type)
////                    arguments[name] = value
////                } ?: vars[name]?.let { v ->
////                    val value = readers[v.name]!!.invoke(newIn, v.type)
////                    toPlace.add(v to value)
////                } ?: run {
////                    skipField()
////                }
////            }
////
////            val instance = constructor.callGiven(arguments)!!
////            instance.setUntyped(toPlace)
////            instance
////        }
////    }
//}