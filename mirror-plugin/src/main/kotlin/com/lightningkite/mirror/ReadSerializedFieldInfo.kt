package com.lightningkite.mirror

class ReadSerializedFieldInfo(
        val name: String,
        val type: ReadType,
        val isOptional: Boolean = false,
        val annotations: List<AnnotationInfo> = listOf(),
        val default: String? = null
) {
    fun toString(owner: ReadClassInfo): String {
        return "val $name = SerializedFieldInfo<${owner.accessNameWithStars}, ${type.useMinimumBound(owner)}>(${owner.reflectionName}, \"$name\", ${type.toString(owner)}, $isOptional, { it.$name as ${type.useMinimumBound(owner)}}, ${annotations.joinToString(", ", "listOf(", ")")})"
    }
}