package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.Type


interface Serializer<T> {
    val contentType: String
    fun <V : Any> write(value: V?, type: Type<V>): T
    fun <V : Any> read(from: T, type: Type<V>): V?
}