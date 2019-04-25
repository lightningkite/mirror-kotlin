package com.lightningkite.mirror.representation

import com.fasterxml.jackson.annotation.JsonIgnore

data class ReadFieldInfo(
        val name: String,
        val type: ReadType,
        val optional: Boolean = false,
        val annotations: List<AnnotationInfo> = listOf(),
        val mutable: Boolean = false,
        val default: String? = null
) {
    val mirrorAnnotations @JsonIgnore get() = annotations.filter { it.name != "Suppress" }
    val fieldName @JsonIgnore get() = "field${name.capitalize()}"
}
