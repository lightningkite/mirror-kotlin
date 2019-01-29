package com.lightningkite.mirror

class DeclarationsFile(val packageName: String, val objectName: String, val reflections: List<String>) {
    override fun toString(): String = """
        |package $packageName
        |
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |fun $objectName() = MirrorClassMirror.register(
        |${reflections.joinToString(",\n    ", "    ")}
        |)
    """.trimMargin()
}