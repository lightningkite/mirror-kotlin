package com.lightningkite.mirror.plugin.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightningkite.mirror.representation.ReadFieldInfo
import com.lightningkite.mirror.representation.ReadType
import com.lightningkite.mirror.representation.ReadTypeProjection
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

    @Test
    fun testField() {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val field = ReadFieldInfo(
                name = "first",
                type = ReadType("String"),
                optional = true,
                annotations = listOf(),
                mutable = true,
                default = "\"default\""
        )
        val str = mapper.writeValueAsString(field)
        println(str)
        val copy = mapper.readValue(str, ReadFieldInfo::class.java)
        println(copy)
        assert(field == copy)
    }
}