package com.lightningkite.kotlinx.reflection.plugin.test


@ExternalReflection
@ExternalName("TestClass")
data class TestClass(
        var a: Int = 42,
        var b: String = "string"
)

@ExternalReflection
data class Post(
        var userId: Long = 0,
        var id: Long = 0,
        var title: String = "",
        var body: String = ""
)
