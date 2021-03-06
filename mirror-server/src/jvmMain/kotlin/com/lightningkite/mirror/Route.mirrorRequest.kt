package com.lightningkite.mirror

import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.mirror.info.AnyMirror
import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.MirrorType
import com.lightningkite.mirror.info.list
import com.lightningkite.mirror.request.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
fun Route.expose(handler: Request.Handler, path: String, suppressStackTrace: Boolean = true): Route {
    return post(path) { _ ->
        try {
            val sf: Request<*> = call.receive<Request<*>>(Request::class)
            val respondType = RemoteResultMirror(sf.returnType as MirrorType<Any?>)
            try {
                val result = handler.invoke(sf)
                @Suppress("UNCHECKED_CAST")
                call.respond<RemoteResult<Any?>>(
                        status = HttpStatusCode.OK,
                        type = respondType,
                        value = RemoteResult(result)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                val isExpected = sf.throwsTypes?.contains(e.javaClass.simpleName) ?: false
                call.respond(
                        status = if (isExpected) HttpStatusCode.BadRequest else HttpStatusCode.InternalServerError,
                        type = respondType,
                        value = RemoteResult(exception = RemoteExceptionData(
                                type = e.javaClass.simpleName,
                                message = e.message ?: "",
                                trace = if (suppressStackTrace) "" else e.stackTraceString(),
                                data = null
                        ))
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}

@ContextDsl
fun Route.exposeMulti(handler: Request.Handler, path: String, suppressStackTrace: Boolean = true): Route {
    return post(path) { _ ->
        try {
            val requests = call.receive<List<Request<Any?>>>(RequestMirror(AnyMirror.nullable).list)
            val respondType = RemoteResultMirror(AnyMirror.nullable).list
            val results = requests.map { sf ->
                try {
                    val result = handler.invoke(sf)
                    RemoteResult<Any?>(result)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    val isExpected = sf.throwsTypes?.contains(e.javaClass.simpleName) ?: false
                    RemoteResult<Any?>(exception = RemoteExceptionData(
                            type = e.javaClass.simpleName,
                            message = e.message ?: "",
                            trace = if (suppressStackTrace) "" else e.stackTraceString(),
                            data = null
                    ))
                }
            }
            call.respond(
                    status = HttpStatusCode.OK,
                    type = respondType,
                    value = results
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}