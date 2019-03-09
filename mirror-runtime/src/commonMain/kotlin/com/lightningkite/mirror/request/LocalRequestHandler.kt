package com.lightningkite.mirror.request

import kotlin.reflect.KClass

class LocalRequestHandler : Request.Handler {

    var defaultInvocation: suspend Any.() -> Any? = {
        throw IllegalArgumentException("No invocation for type ${this::class} is known.")
    }

    val invocations = HashMap<KClass<out Request<*>>, suspend (Any) -> Any?>()
    inline fun <reified R : Request<T>, T> invocation(noinline action: suspend R.() -> T) {
        @Suppress("UNCHECKED_CAST")
        invocations[R::class] = action as suspend (Any) -> Any?
    }

    fun <R : Request<T>, T> invocation(kclass: KClass<R>, action: suspend R.() -> T) {
        @Suppress("UNCHECKED_CAST")
        invocations[kclass] = action as suspend (Any) -> Any?
    }


    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> invoke(request: Request<T>): T {
        val invocation = invocations[request::class] ?: defaultInvocation
        val result = invocation.invoke(request)
        return result as T
    }
}