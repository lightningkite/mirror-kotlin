package com.lightningkite.mirror.info

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
var <T : Any> KClass<T>.info
    get() = ClassInfo[this]
    set(value) {
        ClassInfo.register(value)
    }

private val KClass_type = HashMap<KClass<*>, Type<*>>()
@Suppress("UNCHECKED_CAST")
val <T : Any> KClass<T>.type: Type<T>
    get() = KClass_type.getOrPut(this) { Type<T>(this) } as Type<T>

private val KClass_typeNullable = HashMap<KClass<*>, Type<*>>()
@Suppress("UNCHECKED_CAST")
val <T : Any> KClass<T>.typeNullable: Type<T?>
    get() = KClass_typeNullable.getOrPut(this) { Type<T?>(this, nullable = true) } as Type<T?>