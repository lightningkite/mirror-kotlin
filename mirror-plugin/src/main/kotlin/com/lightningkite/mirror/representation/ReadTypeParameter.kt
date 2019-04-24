package com.lightningkite.mirror.representation

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

fun ReadTypeParameter.useMinimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()) = projection.useMinimumBound(owner, maxResolutions, resolutions)
fun ReadTypeProjection.useMinimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()) = if (this.variance == ReadTypeProjection.Variance.STAR) "*" else type.useMinimumBound(owner, maxResolutions, resolutions)
fun ReadType.useMinimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()): String {
    return if (typeArguments.isEmpty() && owner.typeParameters.any { it.name == this.kclass }) {
        if (resolutions[kclass] ?: 0 >= maxResolutions) {
            //This has already been resolved in the chain.
            "*"
        } else {
            //We need to resolve this once.
            val type = owner.typeParameters.find { it.name == this.kclass }!!.projection.type
            type.useMinimumBound(owner, maxResolutions, resolutions + (this.kclass to resolutions.getOrDefault(this.kclass, 0) + 1))
        }
    } else kclass + (if (typeArguments.isNotEmpty())
        typeArguments.joinToString(", ", "<", ">") {
            it.useMinimumBound(owner, maxResolutions, resolutions)
        }
    else
        "") + if (nullable) "?" else ""
}


fun ReadTypeParameter.minimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()) = projection.minimumBound(owner, maxResolutions, resolutions)
fun ReadTypeProjection.minimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()) = if (this.variance == ReadTypeProjection.Variance.STAR) ReadType("Any", nullable = true) else type.minimumBound(owner, maxResolutions, resolutions)
fun ReadType.minimumBound(owner: ReadClassInfo, maxResolutions: Int = 2, resolutions: Map<String, Int> = mapOf()): ReadType {
    return if (typeArguments.isEmpty() && owner.typeParameters.any { it.name == this.kclass }) {
        if (resolutions[kclass] ?: 0 >= maxResolutions) {
            //This has already been resolved in the chain.
            ReadType("Any", nullable = true)
        } else {
            //We need to resolve this once.
            val type = owner.typeParameters.find { it.name == this.kclass }!!.projection.type
            type.minimumBound(owner, maxResolutions, resolutions + (this.kclass to resolutions.getOrDefault(this.kclass, 0) + 1))
        }
    } else this.copy(typeArguments = typeArguments.map { ReadTypeProjection(it.minimumBound(owner, maxResolutions, resolutions)) })
}

