package com.lightningkite.mirror

import com.lightningkite.mirror.request.Request
import com.lightningkite.mirror.request.RequestHandler
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

class KtorRequestHandlerFactory<USER> {

    fun user(user: USER?) = KtorRequestHandler(user)
    inline fun <reified T : Request<R>, R> invocation(noinline action: suspend (Any, USER?) -> Any?) {
        invocations[T::class] = action
    }

    val invocations = HashMap<KClass<*>, suspend (Any, USER?) -> Any?>()

    @Suppress("UNCHECKED_CAST")
    var <SF : Request<R>, R> KClass<SF>.invocation: suspend SF.(USER?) -> R
        set(value) {
            invocations[this] = value as suspend (Any, USER?) -> Any?
        }
        get() {
            return invocations[this] as suspend SF.(USER?) -> R
        }

    @Suppress("UNCHECKED_CAST")
    suspend operator fun <R> Request<R>.invoke(user: USER?): R = (invocations[this::class]
            ?: throw IllegalArgumentException("No invocation for ${this::class} known.")).invoke(this, user) as R

    inner class KtorRequestHandler(val user: USER?) : RequestHandler {
        override suspend fun <T> invoke(request: Request<T>): T {
            return request.invoke(user)
        }
    }
}