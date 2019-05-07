package com.lightningkite.mirror

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer


object Metrics: MetricRegistry()

inline fun <T> Timer.timeInline(action: ()->T): T{
    val start = java.lang.System.nanoTime()
    val result = action()
    val end = java.lang.System.nanoTime()
    update(end - start, java.util.concurrent.TimeUnit.NANOSECONDS)
    return result
}