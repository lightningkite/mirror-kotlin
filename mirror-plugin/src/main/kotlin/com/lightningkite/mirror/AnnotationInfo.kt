package com.lightningkite.mirror

class AnnotationInfo(
        val name: String,
        val arguments: List<Any?>,
        val useSiteTarget: String? = null
) {
    override fun toString(): String = "AnnotationInfo(\"$name\", ${arguments.joinToString(", ", "listOf(", ")")})"
}