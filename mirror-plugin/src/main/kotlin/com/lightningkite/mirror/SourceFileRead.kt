package com.lightningkite.mirror

import com.lightningkite.mirror.representation.ReadClassInfo

data class SourceFileRead(
        val hash: Int,
        val infos: List<ReadClassInfo>,
        val version: Int = VERSION
) {
    companion object {
        const val VERSION = 8
    }
}