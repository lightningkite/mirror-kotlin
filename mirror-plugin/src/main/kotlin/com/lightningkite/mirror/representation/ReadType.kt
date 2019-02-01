package com.lightningkite.mirror.representation

import com.fasterxml.jackson.annotation.JsonIgnore

data class ReadType(
        val kclass: String = "",
        val typeArguments: List<ReadTypeProjection> = listOf(),
        val nullable: Boolean = false
) {
    val use: String
        @JsonIgnore get() = kclass + (if (typeArguments.isNotEmpty())
            typeArguments.joinToString(",", "<", ">") { it.use }
        else
            "") + if (nullable) "?" else ""

    override fun toString(): String {
        val baseMirror = when{
            kclass.startsWith("kotlin.") -> kclass.substringAfterLast('.') + "Mirror"
            else -> kclass + "Mirror"
        }
        return (if(typeArguments.isEmpty()) {
            baseMirror
        } else {
            "${baseMirror}(${typeArguments.joinToString()})"
        }).let{
            if(nullable){
                it.plus(".nullable")
            } else it
        }
    }
}