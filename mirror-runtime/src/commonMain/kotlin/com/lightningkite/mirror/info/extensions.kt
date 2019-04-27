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

val MirrorClass<*>.allImplements: Sequence<MirrorClass<*>> get() = implements.asSequence().flatMap { sequenceOf(it) + it.allImplements }

fun MirrorType<*>.satisfies(other: MirrorType<*>): MirrorClass<*>? {
    if (!other.isNullable && this.isNullable) return null
    if (other.base == AnyMirror) return this.base

    //Check for directly implementing, can't be base
    if (this.base.typeParameters.none { it is TypeArgumentMirrorType } && this isA other) return this.base

    //Check for matching by modifying type parameter
    val bk = other.base.kClass
    val similar = this.base.allImplements.find { it.base.kClass == bk } ?: return null
    if (similar.typeParameters.none { it is TypeArgumentMirrorType }) return null

    val mirrorCompanion = this.base.mirrorClassCompanion ?: return null

    val params = mirrorCompanion.minimal.typeParameters.toList().mapNotNull { it as? TypeArgumentMirrorType }.toTypedArray()
    if (!params.apply(similar, other)) return null
    return mirrorCompanion.make(params.map { it.minimal })
}

private fun Array<TypeArgumentMirrorType<*>>.apply(from: MirrorType<*>, to: MirrorType<*>): Boolean {
    if (!from.isNullable && to.isNullable) return false

    if (from is TypeArgumentMirrorType<*>) {
        val index = this.indexOfFirst { it.typeArgumentName == from.typeArgumentName }
        if (index == -1) {
            return false
        }
        val myFrom = this[index]
        if (to isA myFrom.minimal) {
            this[index] = TypeArgumentMirrorType(myFrom.typeArgumentName, myFrom.variance, to)
            return true
        } else {
            return false
        }
    }

    val goalTypeParameters = to.base.typeParameters
    val minimalTypeParameters = from.base.typeParameters

    for (index in goalTypeParameters.indices) {
        if (!apply(minimalTypeParameters[index], goalTypeParameters[index])) {
            return false
        }
    }
    return true
}

infix fun MirrorType<*>.isA(other: MirrorType<*>): Boolean {
    if (!other.isNullable && this.isNullable) return false
    if (other.base == AnyMirror) return true
//    if (this == other) return true
    if (this.base.kClass == other.base.kClass) {
        val typeArgumentVariances = other.base.mirrorClassCompanion?.minimal?.typeParameters?.toList() as? List<TypeArgumentMirrorType<*>>
                ?: return true
        for(index in typeArgumentVariances.indices){
            when(typeArgumentVariances[index].variance){
                Variance.IN -> {
                    //In requires a more general type
                    if(!(other.base.typeParameters[index] isA this.base.typeParameters[index]))
                        return false
                }
                Variance.OUT -> {
                    //In requires a more specific type
                    if(!(this.base.typeParameters[index] isA other.base.typeParameters[index]))
                        return false
                }
                Variance.INVARIANT -> {
                    //Invariant requires an exact match
                    if(this.base.typeParameters[index] != other.base.typeParameters[index])
                        return false
                }
                Variance.STAR -> {}
            }
        }
        return true
    }
    return this.base.implements.any { it isA other }
}