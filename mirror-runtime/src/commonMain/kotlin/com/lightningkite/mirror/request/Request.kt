package com.lightningkite.mirror.request

interface Request<out T> {
    interface Handler {
        suspend fun <T> invoke(request: Request<T>): T
    }
}

