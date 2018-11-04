package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.info
import kotlin.reflect.KClass

//annotation class ExternalName(val name:String)

private val KClass_externalName = HashMap<KClass<*>, String>()
val KClassesByExternalName = HashMap<String, KClass<*>>()
var KClass<*>.externalName: String
    get() {
        return KClass_externalName.getOrPut(this) {
            val newName = info.let {
                if (it.owner == null) it.name
                else it.owner!!.info.name + "." + it.name
            }
            KClassesByExternalName[newName] = this
            newName
        }
    }
    set(value) {
        KClassesByExternalName[value] = this
        KClass_externalName[this] = value
    }