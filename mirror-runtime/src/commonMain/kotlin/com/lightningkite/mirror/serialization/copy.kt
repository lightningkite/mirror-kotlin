package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.ClassInfo

fun <T : Any> T.copy(type: ClassInfo<T>): T {
    return type.construct(type.fields.associate { it.name to it.get(this) })
}