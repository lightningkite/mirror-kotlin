package com.lightningkite.mirror

data class ReadTypeParameter(
        val name: String,
        val projection: ReadTypeProjection
) {
//    override fun toString(): String = "TypeProjection($projection, TypeProjection.Variance.$variance)"

//    val use: String
//        get() = kClass + (if (typeArguments.isNotEmpty())
//            typeArguments.joinToString(",", "<", ">") { it.use }
//        else
//            "") + if (isNullable) "?" else ""

}

class TestType<T : Comparable<T>> {
    companion object {
        fun instance() = TestType<Comparable<Comparable<*>>>()
    }
}

fun ReadTypeParameter.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()) = projection.useMinimumBound(owner, resolutions)
fun ReadTypeProjection.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()) = type.useMinimumBound(owner, resolutions)
fun ReadType.useMinimumBound(owner: ReadClassInfo, resolutions: Map<String, Int> = mapOf()): String {
    return if (typeArguments.isEmpty() && owner.typeParameters.any { it.name == this.kClass }) {
        if (resolutions[kClass] ?: 0 >= 2) {
            //This has already been resolved in the chain.
            "*"
        } else {
            //We need to resolve this once.
            owner.typeParameters.find { it.name == this.kClass }!!.projection.type.useMinimumBound(owner, resolutions + (this.kClass to resolutions.getOrDefault(this.kClass, 0) + 1))
        }
    } else kClass + (if (typeArguments.isNotEmpty())
        typeArguments.joinToString(",", "<", ">") {
            it.type.useMinimumBound(owner, resolutions)
        }
    else
        "") + if (isNullable) "?" else ""
}
