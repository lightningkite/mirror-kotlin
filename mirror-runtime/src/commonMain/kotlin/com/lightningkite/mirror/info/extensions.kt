package com.lightningkite.mirror.info

val <Type> MirrorType<Type>.list get() = ListMirror(this)
infix fun <KeyType, ValueType> MirrorType<KeyType>.mapTo(other:MirrorType<ValueType>) = MapMirror(this, other)

@Suppress("UNCHECKED_CAST")
val <Type> MirrorType<Type>.nullable: MirrorType<Type?>
    get() = when (this) {
        is MirrorClass<*> -> NullableMirrorType(this) as MirrorType<Type?>
        is NullableMirrorType<*> -> this as MirrorType<Type?>
        else -> throw IllegalArgumentException()
    }