package com.lightningkite.mirror

import com.lightningkite.mirror.info.MirrorType
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import java.util.*

private val ApplicationCallReceiveMirrorType = WeakHashMap<ApplicationCall, MirrorType<*>>()
var ApplicationCall.receiveMirrorType: MirrorType<*>?
    get() = ApplicationCallReceiveMirrorType[this]
    set(value) {
        ApplicationCallReceiveMirrorType[this] = value
    }

suspend fun <T : Any> ApplicationCall.receive(type: MirrorType<*>): T {
    this.receiveMirrorType = type
    @Suppress("UNCHECKED_CAST")
    return receive(type.base.kClass) as T
}


suspend fun <T> ApplicationCall.respond(type: MirrorType<T>, value: T) {
    return respond(StrictlyTypedWrapper(type, value))
}

suspend fun <T> ApplicationCall.respond(status: HttpStatusCode, type: MirrorType<T>, value: T) {
    return respond(status, StrictlyTypedWrapper(type, value))
}


