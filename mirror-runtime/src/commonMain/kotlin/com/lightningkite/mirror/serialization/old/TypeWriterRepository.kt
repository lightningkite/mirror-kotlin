//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.Type
//import kotlin.reflect.KClass
//
//interface TypeWriterRepository<OUT, RESULT> {
//    fun writer(type: KClass<*>): TypeWriter<OUT, RESULT>
//}
//
//typealias TypeWriter<OUT, RESULT> = OUT.(Any?, Type<*>) -> RESULT
//typealias TypeWriterGenerator<OUT, RESULT> = (KClass<*>) -> TypeWriter<OUT, RESULT>?
//
///*
//
//TypeWriterRepository - Something that gives writers for KClassesByExternalName
//TypeWriter - Something that writes a particular KClass
//WritesTo - Something that can write anything to a particular format
//
//*/