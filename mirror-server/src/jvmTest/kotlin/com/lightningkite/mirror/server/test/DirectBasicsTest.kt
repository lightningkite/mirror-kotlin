package com.lightningkite.mirror.server.test

import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.mirror.*
import com.lightningkite.mirror.info.*
import com.lightningkite.mirror.expose
import com.lightningkite.mirror.request.RemoteExceptionData
import com.lightningkite.mirror.request.Request
import com.lightningkite.mirror.request.RequestMirror
import com.lightningkite.mirror.request.RemoteResultMirror
import com.lightningkite.mirror.request.LocalRequestHandler
import com.lightningkite.mirror.request.registerRequests
import com.lightningkite.mirror.server.registerMirrorServerTest
import io.ktor.application.Application
import kotlinx.serialization.json.Json
import org.junit.Test
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication

class DirectBasicsTest {
    val handler = LocalRequestHandler().apply {
        invocation(ThrowExceptionRequest::class) {
            println("I'm going to die here.")
            throw ForbiddenException("NOPE")
        }
        invocation(PingRequest::class) {
            "Hello, $name!"
        }
    }
    val serializer = Json()

    init {
        registerRequests()
        registerMirrorServerTest()
    }

    val setupTestApplication: Application.() -> Unit = {
        install(ContentNegotiation) {
            register(serializer)
        }
        install(StatusPages) {
            status(HttpStatusCode.NotFound) {
                call.respond("Nothing here")
            }
            exception<Exception> {
                call.respond("Throwing error:\n ${it.stackTraceString()}")
            }
        }
        routing {
            println("Setting up server function")
            get("hello") {
                call.respondText("HYPE", ContentType.Text.Plain, HttpStatusCode.Accepted)
            }
            expose(handler, "request", false)
        }
    }

    @Test
    fun throwing() = withTestApplication(setupTestApplication) {
        println("Beginning test")
        with(handleRequest(HttpMethod.Post, "/request") {
            val body = serializer.stringify(RequestMirror.minimal, ThrowExceptionRequest() as Request<Any?>)
            println(body)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.setBody(body)
        }) {
            println("out: " + response.status())
            println("out: " + response.content)
            val out = serializer.parse(RemoteResultMirror(UnitMirror), response.content ?: "")
            println("out: " + out)
        }
    }

    @Test
    fun pinging() = withTestApplication(setupTestApplication) {
        println("Beginning test")
        with(handleRequest(HttpMethod.Post, "/request") {
            val body = serializer.stringify(RequestMirror.minimal, PingRequest("Joseph") as Request<Any?>)
            println(body)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.setBody(body)
        }) {
            println("out: " + response.status())
            println("out: " + response.content)
            val out = serializer.parse(RemoteResultMirror(StringMirror), response.content ?: "")
            println("out: " + out)
        }
    }
}