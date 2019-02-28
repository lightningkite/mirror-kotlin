package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.MirrorAnnotation
import com.lightningkite.mirror.info.MirrorClassMirror
import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.MirrorType
import kotlin.reflect.KClass

annotation class RequiresPermission(vararg val permissions: String)

fun Request<*>.permitted(user: HasPermissions?): Boolean {
    val p = this::class.permissions
    return if (p.isEmpty()) true
    else p.containsAll(user?.permissions ?: setOf())
}

@Suppress("UNCHECKED_CAST")
val KClass<out Request<*>>.permissions: Set<String>
    get() {
        return MirrorRegistry[this]!!.annotations
                .mapNotNull { it as? MirrorAnnotation }
                .find { it.annotationType == RequiresPermission::class }?.asMap()?.map { it.toString() }?.toSet()
                ?: setOf()
    }

