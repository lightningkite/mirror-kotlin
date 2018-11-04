//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.mirror.info.info
//import com.lightningkite.mirror.info.type
//
//object EnumGenerators {
//
//    fun <IN> readerGenerator(forReader: StandardReaderRepository<IN>): TypeReaderGenerator<IN> = generator@{ type ->
//        val mapped = type.info.enumValues?.associate { (it as Enum<*>).name.toLowerCase() to it }
//                ?: return@generator null
//
//        //Cache the reader
//        val stringReader = forReader.reader(String::class)
//        val stringType = String::class.type
//
//        return@generator { _ ->
//            val name = stringReader.invoke(this, stringType).let { it as String }.toLowerCase()
//            mapped[name] ?: throw SerializationException("Enum value $name not recognized.")
//        }
//    }
//
//    fun <OUT, RESULT> writerGenerator(
//            forWriter: StandardWriterRepository<OUT, RESULT>
//    ): TypeWriterGenerator<OUT, RESULT> = generator@{ type ->
//        if (type.info.enumValues == null) return@generator null
//
//        //Cache the writer
//        val stringWriter = forWriter.writer(String::class)
//        val stringType = String::class.type
//
//        return@generator { value, _ ->
//            stringWriter.invoke(this, (value as Enum<*>).name, stringType)
//        }
//    }
//}