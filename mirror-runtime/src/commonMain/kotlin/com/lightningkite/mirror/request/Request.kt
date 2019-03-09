package com.lightningkite.mirror.request

interface Request<T> {
    interface Handler {
        suspend fun <T> invoke(request: Request<T>): T
    }
}

