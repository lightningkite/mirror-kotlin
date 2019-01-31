package com.lightningkite.mirror.info

val <Type> MirrorType<Type>.list get() = ListMirror(this)
infix fun <KeyType, ValueType> MirrorType<KeyType>.mapTo(other:MirrorType<ValueType>) = MapMirror(this, other)