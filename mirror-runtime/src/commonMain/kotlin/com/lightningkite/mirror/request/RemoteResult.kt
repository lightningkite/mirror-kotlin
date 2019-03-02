package com.lightningkite.mirror.request

data class RemoteResult<T>(
        val resultOrNull: T? = null,
        val exception: RemoteExceptionData? = null
) {
    val success: Boolean get() = exception == null
    val result: T
        get() {
            if (exception != null) throw RemoteExceptionData.Thrown(exception)
            @Suppress("UNCHECKED_CAST")
            return resultOrNull as T
        }
}