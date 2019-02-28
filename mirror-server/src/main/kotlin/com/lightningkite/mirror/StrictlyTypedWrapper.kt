package com.lightningkite.mirror

import com.lightningkite.mirror.info.MirrorType

data class StrictlyTypedWrapper<T>(
        val type: MirrorType<T>,
        val value: T
)