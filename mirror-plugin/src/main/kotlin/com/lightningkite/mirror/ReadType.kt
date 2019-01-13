package com.lightningkite.mirror

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

    fun resolveMinimumKClass(owner: ReadClassInfo): String = owner.typeParameters.find { it.name == kclass }?.projection?.type?.kclass
            ?: kclass

    fun toString(owner: ReadClassInfo) = "Type<${useMinimumBound(owner)}>(${resolveMinimumKClass(owner)}::class, listOf(${typeArguments.joinToString{it.toString(owner)}}), $nullable)"

    override fun toString(): String {
        return ""
    }
}