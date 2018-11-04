package com.lightningkite.mirror.info

data class SerializedFieldInfo<Owner : Any, T>(
        val owner: ClassInfo<Owner>,
        val name: String,
        val type: Type<T>,
        val isOptional: Boolean,
        val get: (Owner) -> T?,
        val annotations: List<AnnotationInfo> = listOf()
)