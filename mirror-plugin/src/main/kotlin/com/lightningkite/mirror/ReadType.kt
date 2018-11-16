package com.lightningkite.mirror

data class ReadType(
        val kClass: String,
        val typeArguments: List<ReadTypeProjection> = listOf(),
        val isNullable: Boolean = false
) {
    val use: String
        get() = kClass + (if (typeArguments.isNotEmpty())
            typeArguments.joinToString(",", "<", ">") { it.use }
        else
            "") + if (isNullable) "?" else ""

    fun resolveMinimumKClass(owner: ReadClassInfo): String = owner.typeParameters.find { it.name == kClass }?.projection?.type?.kClass
            ?: kClass

    fun toString(owner: ReadClassInfo) = "Type<${useMinimumBound(owner)}>(${resolveMinimumKClass(owner)}::class, listOf(${typeArguments.joinToString{it.toString(owner)}}), $isNullable)"

    override fun toString(): String {
        return ""
    }
}