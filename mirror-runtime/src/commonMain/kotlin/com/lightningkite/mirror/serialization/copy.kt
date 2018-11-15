package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.ClassInfo

fun <T : Any> T.copy(type: ClassInfo<T>): T {
    return type.construct(type.fields.associate { it.name to it.get(this) })
}

fun <T : Any> T.toAttributeMap(type: ClassInfo<T>): Map<String, Any?> = type.fields.associate { it.name to it.get(this) }
fun <T : Any> T.toAttributeHashMap(type: ClassInfo<T>): HashMap<String, Any?> =
        type.fields.associateTo(HashMap()) { it.name to it.get(this) }