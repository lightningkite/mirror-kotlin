package com.lightningkite.mirror

class ReadFieldInfo(
        val name: String,
        val type: ReadType,
        val isOptional: Boolean = false,
        val annotations: List<AnnotationInfo> = listOf(),
        val default: String? = null
) {
    val fieldName = "field${name.capitalize()}"
    fun toString(owner: ReadClassInfo): String {
        return "val $fieldName = FieldInfo<${owner.accessNameWithStars}, ${type.useMinimumBound(owner)}>(this, \"$name\", ${type.toString(owner)}, $isOptional, { it.$name as ${type.useMinimumBound(owner)}}, ${annotations.joinToString(", ", "listOf(", ")")})"
    }
}
