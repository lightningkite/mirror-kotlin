package com.lightningkite.mirror

class DeclarationsFile(val packageName: String, val functionName: String, val reflections: List<String>) {
    override fun toString(): String = """
        package $packageName

        import com.lightningkite.mirror.info.*
        import kotlin.reflect.KClass

        fun $functionName(){
        ${reflections.joinToString("\n    ", "    ") { "ClassInfo.register($it)" }}
        }
    """.trimIndent()
}