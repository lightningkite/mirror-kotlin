package com.lightningkite.mirror.representation

import com.fasterxml.jackson.annotation.JsonIgnore

data class ReadType(
        val kclass: String = "",
        val typeArguments: List<ReadTypeProjection> = listOf(),
        val nullable: Boolean = false
) {
    val use: String
        @JsonIgnore get() = nnUse + if (nullable) "?" else ""

    val nnUse: String
        @JsonIgnore get() = kclass + (if (typeArguments.isNotEmpty())
            typeArguments.joinToString(", ", "<", ">") { it.use }
        else
            "")

    override fun toString(): String {
        val baseMirror = when {
            kclass.startsWith("kotlin.") -> kclass.substringAfterLast('.') + "Mirror"
            else -> kclass + "Mirror"
        }
        val fixedBaseMirror = baseMirror.split('.').let {
            val firstClassIndex = it.indexOfFirst { it.firstOrNull()?.isUpperCase() == true }
            it.subList(0, firstClassIndex + 1).joinToString(".") + it.subList(firstClassIndex + 1, it.size).joinToString("")
        }
        return (if (typeArguments.isEmpty()) {
            fixedBaseMirror
        } else if (typeArguments.any { it.variance == ReadTypeProjection.Variance.STAR }) {
            "($fixedBaseMirror.make(${typeArguments.joinToString()}) as MirrorType<$nnUse>)"
        } else {
            "$fixedBaseMirror(${typeArguments.joinToString()})"
        }).let {
            if (nullable) {
                it.plus(".nullable")
            } else it
        }
    }
}