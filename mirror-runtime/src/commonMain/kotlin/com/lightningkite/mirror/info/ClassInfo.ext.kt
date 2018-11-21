package com.lightningkite.mirror.info

val ClassInfo<*>.canBeExtended: Boolean
    get() = modifiers.contains(ClassInfo.Modifier.Open) ||
            modifiers.contains(ClassInfo.Modifier.Interface) ||
            modifiers.contains(ClassInfo.Modifier.Abstract) ||
            modifiers.contains(ClassInfo.Modifier.Sealed)

val ClassInfo<*>.canBeInstantiated: Boolean
    get() = !(modifiers.contains(ClassInfo.Modifier.Interface) ||
            modifiers.contains(ClassInfo.Modifier.Abstract) ||
            modifiers.contains(ClassInfo.Modifier.Sealed))

val ClassInfo<*>.localName: String get() = this.ownerName?.let{ "$it.$name" } ?: name
val ClassInfo<*>.qualifiedName: String get() = "$packageName.$localName"