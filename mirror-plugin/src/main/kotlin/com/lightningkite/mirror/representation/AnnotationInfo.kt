package com.lightningkite.mirror.representation

class AnnotationInfo(
        val name: String,
        val arguments: List<String>,
        val useSiteTarget: String? = null
) {
    override fun toString(): String = "AnnotationInfo(\"$name\", ${arguments.joinToString(", ", "listOf(", ")")})"
}