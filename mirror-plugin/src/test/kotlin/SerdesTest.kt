package com.lightningkite.mirror.plugin.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightningkite.mirror.ReadType
import com.lightningkite.mirror.ReadTypeProjection
import org.junit.Test

class SerdesTest {
    @Test
    fun testType() {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val type = ReadType(
                kclass = "kotlin.collection.List",
                typeArguments = listOf(ReadTypeProjection(ReadType("kotlin.Any"), ReadTypeProjection.Variance.INVARIANT)),
                nullable = true
        )
        val str = mapper.writeValueAsString(type)
        println(str)
        val copy = mapper.readValue(str, ReadType::class.java)
        println(copy)
        assert(type.kclass == copy.kclass)
        assert(type.nullable == copy.nullable)
        assert(type.typeArguments .size== copy.typeArguments.size)
    }
}