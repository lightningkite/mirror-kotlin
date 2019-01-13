package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.ClassInfoRegistry
import com.lightningkite.mirror.info.Type
import com.lightningkite.mirror.info.allImplements
import kotlin.reflect.KClass


fun Request<*>.permitted(registry: ClassInfoRegistry, user: HasPermissions?): Boolean {
    val p = this::class.permissions(registry)
    return if(p.isEmpty()) true
    else p.containsAll(user?.permissions ?: setOf())
}

@Suppress("UNCHECKED_CAST")
fun <T> KClass<out Request<T>>.returnType(registry: ClassInfoRegistry): Type<T> {
    return registry.getOrThrow(this)
            .allImplements(registry)
            .find { it.kClass == Request::class }!!
            .typeParameters.first().type as Type<T>
}

@Suppress("UNCHECKED_CAST")
fun KClass<out Request<*>>.permissions(registry: ClassInfoRegistry):Set<String> {
    return registry.getOrThrow(this).annotations.find { it.name.endsWith("RequiresPermission") }?.arguments?.map { it.toString() }?.toSet() ?: setOf()
}