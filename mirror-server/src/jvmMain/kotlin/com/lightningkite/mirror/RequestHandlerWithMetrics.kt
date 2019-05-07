package com.lightningkite.mirror

import com.codahale.metrics.MetricRegistry
import com.lightningkite.mirror.request.Request

class RequestHandlerWithMetrics(val underlying: Request.Handler, val metrics: MetricRegistry = Metrics): Request.Handler {
    override suspend fun <T> invoke(request: Request<T>): T {
        return metrics.timer(request::class.qualifiedName).timeInline { underlying.invoke(request) }
    }
}
fun Request.Handler.withMetrics(metrics: MetricRegistry = Metrics) = RequestHandlerWithMetrics(this, metrics)