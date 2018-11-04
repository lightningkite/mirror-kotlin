package com.lightningkite.mirror.info

private val KxClass_allImplements = HashMap<ClassInfo<*>, List<Type<*>>>()
val ClassInfo<*>.allImplements: List<Type<*>>
    get() = KxClass_allImplements.getOrPut(this) {
        implements + implements.flatMap { it.kClass.info.allImplements }.distinctBy { it.kClass }
    }

val ClassInfo<*>.canBeExtended: Boolean
    get() = modifiers.contains(ClassInfo.Modifier.Open) ||
            modifiers.contains(ClassInfo.Modifier.Interface) ||
            modifiers.contains(ClassInfo.Modifier.Abstract) ||
            modifiers.contains(ClassInfo.Modifier.Sealed)

val ClassInfo<*>.canBeInstantiated: Boolean
    get() = !(modifiers.contains(ClassInfo.Modifier.Interface) ||
            modifiers.contains(ClassInfo.Modifier.Abstract) ||
            modifiers.contains(ClassInfo.Modifier.Sealed))