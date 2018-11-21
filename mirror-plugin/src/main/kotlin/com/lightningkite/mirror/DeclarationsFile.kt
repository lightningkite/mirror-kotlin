package com.lightningkite.mirror

class DeclarationsFile(val packageName: String, val objectName: String, val reflections: List<String>) {
    override fun toString(): String = """
        |package $packageName
        |
        |import com.lightningkite.kommon.native.SharedImmutable
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |@SharedImmutable
        |val $objectName = ClassInfoRegistry(
        |${reflections.joinToString(",\n    ", "    ")}
        |)
    """.trimMargin()
}