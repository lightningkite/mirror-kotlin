package com.lightningkite.mirror.info

val <Type> MirrorType<Type>.list get() = ListMirror(this)
infix fun <KeyType, ValueType> MirrorType<KeyType>.mapTo(other: MirrorType<ValueType>) = MapMirror(this, other)

@Suppress("UNCHECKED_CAST")
val <Type> MirrorType<Type>.nullable: MirrorType<Type?>
    get() = when (this) {
        is MirrorClass<*> -> NullableMirrorType(this) as MirrorType<Type?>
        is NullableMirrorType<*> -> this as MirrorType<Type?>
        is TypeArgumentMirrorType<*> -> (this.minimal as MirrorType<Type>).nullable
        else -> throw IllegalArgumentException()
    }

fun MirrorType<*>.satisfies(other: MirrorType<*>): MirrorType<*>? {
    if (!other.isNullable && this.isNullable) return null

    val mirrorCompanion = this.base.mirrorClassCompanion ?: return null
    val params = mirrorCompanion.minimal.typeParameters.toList().mapNotNull { it as? TypeArgumentMirrorType }.toTypedArray()
    if(!params.apply(this, other)) return null
    return mirrorCompanion.make(params.map { it.minimal })
}

fun Array<TypeArgumentMirrorType<*>>.apply(from: MirrorType<*>, to: MirrorType<*>): Boolean {
    if (!from.isNullable && to.isNullable) return false

    if (from is TypeArgumentMirrorType<*>) {
        val index = this.indexOfFirst { it.typeArgumentName == from.typeArgumentName }
        if(index == -1){
            return false
        }
        val myFrom = this[index]
        if (to isA myFrom.minimal) {
            this[index] = TypeArgumentMirrorType(myFrom.typeArgumentName, to)
            return true
        } else {
            return false
        }
    }

    val goalTypeParameters = to.base.typeParameters
    val minimalTypeParameters = from.base.typeParameters

    for(index in goalTypeParameters.indices){
        if(!apply(minimalTypeParameters[index], goalTypeParameters[index])){
            return false
        }
    }
    return true
}

infix fun MirrorType<*>.isA(other: MirrorType<*>): Boolean {
    if(other.base == AnyMirror) return true
    if (this == other) return true
    if (!other.isNullable && this.isNullable) return false
    return this.base.implements.any { it isA other }
}