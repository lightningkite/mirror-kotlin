package com.lightningkite.mirror.test


data class Post(
        var id: Long? = null,
        var userId: Long = 0,
        var title: String = "",
        var body: String = ""
)

enum class TestEnum {
    ValueA, ValueB, ValueC
}