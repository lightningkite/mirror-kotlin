package com.lightningkite.mirror.info

interface MirrorClassCompanion {
    val minimal: MirrorClass<*>
    fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*>
}