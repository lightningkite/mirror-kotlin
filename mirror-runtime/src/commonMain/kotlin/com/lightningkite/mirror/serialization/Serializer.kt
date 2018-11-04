package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.Type


interface Serializer<T> {
    val contentType: String
    fun <V> write(value: V, type: Type<V>): T
    fun <V> read(from: T, type: Type<V>): V
}