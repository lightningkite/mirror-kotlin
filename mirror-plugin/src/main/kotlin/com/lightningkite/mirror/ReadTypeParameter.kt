package com.lightningkite.mirror

data class ReadTypeParameter(
        val name: String = "",
        val projection: ReadTypeProjection = ReadTypeProjection()
) {
//    override fun toString(): String = "TypeProjection($projection, TypeProjection.Variance.$variance)"

//    val use: String
//        get() = kClass + (if (typeArguments.isNotEmpty())
//            typeArguments.joinToString(",", "<", ">") { it.use }
//        else
//            "") + if (nullable) "?" else ""

}

fun ReadTypeParameter.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()) = projection.useMinimumBound(owner, resolutions)
fun ReadTypeProjection.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()) = if(this.variance == ReadTypeProjection.Variance.STAR) "*" else type.useMinimumBound(owner, resolutions)
fun ReadType.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()): String {
    return if (typeArguments.isEmpty() && owner.typeParameters.any { it.name == this.kclass }) {
        if (resolutions[kclass] ?: 0 >= 2) {
            //This has already been resolved in the chain.
            "*"
        } else {
            //We need to resolve this once.
            val type = owner.typeParameters.find { it.name == this.kclass }!!.projection.type
            type.useMinimumBound(owner, resolutions + (this.kclass to resolutions.getOrDefault(this.kclass, 0) + 1))
        }
    } else kclass + (if (typeArguments.isNotEmpty())
        typeArguments.joinToString(", ", "<", ">") {
            it.useMinimumBound(owner, resolutions)
        }
    else
        "") + if (nullable) "?" else ""
}

