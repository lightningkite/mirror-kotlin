package com.lightningkite.mirror.request

class RemoteExceptionData(
        var type: String = "",
        var message: String = "",
        var trace: String = "",
        var data: Any? = null
) {
    override fun toString(): String = "$type: $message $data\n$trace"

    class Thrown(val source: RemoteExceptionData) : Exception(source.message) {
        val type: String get() = source.type
        val trace: String get() = source.trace
        val data: Any? get() = source.data
    }
}