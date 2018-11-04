//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.ClassInfo
//import com.lightningkite.mirror.info.SerializedFieldInfo
//import com.lightningkite.mirror.info.info
//import com.lightningkite.mirror.info.type
//import kotlin.reflect.KClass
//
//object CommonSerialization {
//    val defaultReaders = HashMap<KClass<*>, (TypeReaderRepository<Any?>) -> TypeReader<Any?>>()
//    val defaultWriters = HashMap<KClass<*>, (TypeWriterRepository<Any?, Any?>) -> TypeWriter<Any?, Any?>>()
//    val defaultReaderGenerators = ArrayList<Pair<Float, (TypeReaderRepository<Any?>, type: KClass<*>) -> TypeReader<Any?>?>>()
//    val defaultWriterGenerators = ArrayList<Pair<Float, (TypeWriterRepository<Any?, Any?>, type: KClass<*>) -> TypeWriter<Any?, Any?>?>>()
//
//    @Suppress("UNCHECKED_CAST")
//    fun <IN> getDirectSubReader(
//            typeReaderRepository: TypeReaderRepository<IN>,
//            type: KClass<*>
//    ): TypeReader<IN>? = defaultReaders[type]?.invoke(typeReaderRepository as TypeReaderRepository<Any?>)
//
//    @Suppress("UNCHECKED_CAST")
//    fun <OUT, RESULT> getDirectSubWriter(
//            typeWriterRepository: TypeWriterRepository<OUT, RESULT>,
//            type: KClass<*>
//    ): TypeWriter<OUT, RESULT>? = defaultWriters[type]?.invoke(typeWriterRepository as TypeWriterRepository<Any?, Any?>) as? TypeWriter<OUT, RESULT>
//
//    init {
//        defaultReaders[SerializedFieldInfo::class] = gen@{ givenReader ->
//            val stringReader = givenReader.reader(String::class)
//            val stringType = String::class.type
//            return@gen reader@{ type ->
//                val varOnClass = type.typeParameters.getOrNull(0)?.type?.kClass
//                if (varOnClass?.serializePolymorphic == true) {
//                    val propertyName = stringReader.invoke(this, stringType) as String
//                    varOnClass.info.fields[propertyName]!!
//                } else {
//                    val fullName = stringReader.invoke(this, stringType) as String
//                    val className = fullName.substringBeforeLast('.')
//                    val propertyName = fullName.substringAfterLast('.')
//                    KClassesByExternalName[className]!!.info.fields[propertyName]!!
//                }
//            }
//        }
//        defaultWriters[SerializedFieldInfo::class] = gen@{ givenWriter ->
//            val stringWriter = givenWriter.writer(String::class)
//            val stringType = String::class.type
//            return@gen writer@{ value, type ->
//                val variable = value as SerializedFieldInfo<*, *>
//                val varOnClass = type.typeParameters.getOrNull(0)?.type?.kClass
//                val reference = if (varOnClass?.serializePolymorphic == true) {
//                    variable.name
//                } else {
//                    variable.owner.kClass.externalName + "." + variable.name
//                }
//                stringWriter.invoke(this, reference, stringType)
//            }
//        }
//        defaultReaders[ClassInfo::class] = gen@{ givenReader ->
//            val stringReader = givenReader.reader(String::class)
//            val stringType = String::class.type
//            return@gen reader@{ type ->
//                val name = stringReader.invoke(this, stringType) as String
//                KClassesByExternalName[name]!!
//            }
//        }
//        defaultWriters[ClassInfo::class] = gen@{ givenWriter ->
//            val stringWriter = givenWriter.writer(String::class)
//            val stringType = String::class.type
//            return@gen writer@{ value, type ->
//                val info = value as ClassInfo<*>
//                stringWriter.invoke(this, info.kClass.externalName, stringType)
//            }
//        }
//    }
//}
