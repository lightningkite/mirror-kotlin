package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.canBeInstantiated
import com.lightningkite.mirror.info.info
import kotlin.reflect.KClass

private val KClassSerializePolymorphic = HashMap<KClass<*>, Boolean>()
var KClass<*>.serializePolymorphic: Boolean
    get() = KClassSerializePolymorphic.getOrPut(this) {
        !this.info.canBeInstantiated
    }
    set(value) {
        KClassSerializePolymorphic[this] = value
    }