package com.lightningkite.mirror.request

interface RequestHandler {
    suspend fun <T> invoke(request: Request<T>): T
}

