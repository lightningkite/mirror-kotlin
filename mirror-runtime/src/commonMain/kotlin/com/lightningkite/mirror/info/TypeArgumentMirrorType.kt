package com.lightningkite.mirror.info


data class TypeArgumentMirrorType<T>(
        val typeArgumentName: String,
        val variance: Variance,
        val minimal: MirrorType<T>
): MirrorType<T> by minimal

