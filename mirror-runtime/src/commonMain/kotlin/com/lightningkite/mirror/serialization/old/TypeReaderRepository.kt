//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.Type
//import kotlin.reflect.KClass
//
//interface TypeReaderRepository<IN> {
//    fun reader(type: KClass<*>): TypeReader<IN>
//}
//typealias TypeReader<IN> = IN.(Type<*>) -> Any?
//typealias TypeReaderGenerator<IN> = (KClass<*>) -> TypeReader<IN>?