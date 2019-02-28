package com.lightningkite.mirror.server.test

import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.mirror.KtorRequestHandlerFactory
import com.lightningkite.mirror.MirrorStringSerializerConverter
import com.lightningkite.mirror.PrincipalWrapper
import com.lightningkite.mirror.info.ListMirror
import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.StringMirror
import com.lightningkite.mirror.mirrorRequest
import com.lightningkite.mirror.request.RemoteExceptionData
import com.lightningkite.mirror.request.Request
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

class BasicsTest {
    val handler = KtorRequestHandlerFactory<Unit>()
    val serializer = Json()

    init {
        registerMirrorServerTest()
        with(handler) {
            ThrowExceptionRequest::class.invocation = {
                println("I'm going to die here.")
                throw ForbiddenException("NOPE")
            }
            PingRequest::class.invocation = {
                "Hello, $name!"
            }
        }
    }

    val setupTestApplication: Application.() -> Unit = {
        install(ContentNegotiation) {
            val converter = MirrorStringSerializerConverter(serializer, ContentType.Application.Json)
            register(converter.contentType, converter)
        }
        PartData
        install(StatusPages) {
            status(HttpStatusCode.NotFound) {
                call.respond("Nothing here")
            }
            exception<Exception> {
                call.respond("Throwing error:\n ${it.stackTraceString()}")
            }
        }
        install(Authentication) {
            basic {
                validate {
                    PrincipalWrapper(Unit)
                }
            }
        }
        routing {
            println("Setting up server function")
            get("hello") {
                call.respondText("HYPE", ContentType.Text.Plain, HttpStatusCode.Accepted)
            }
            mirrorRequest(handler, "request", false)
        }
    }

    @Test
    fun throwing() = withTestApplication(setupTestApplication) {
        println("Beginning test")
        with(handleRequest(HttpMethod.Post, "/request") {
            val body = serializer.stringify(MirrorRegistry[Request::class]!!, ThrowExceptionRequest())
            println(body)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.setBody(body)
        }) {
            println("out: " + response.status())
            println("out: " + response.content)
            val out = serializer.parse(MirrorRegistry[RemoteExceptionData::class]!!, response.content ?: "")
            println("out: " + out)
        }
    }

    @Test
    fun pinging() = withTestApplication(setupTestApplication) {
        println("Beginning test")
        with(handleRequest(HttpMethod.Post, "/request") {
            val body = serializer.stringify(MirrorRegistry[Request::class]!!, PingRequest("Joseph"))
            println(body)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.setBody(body)
        }) {
            println("out: " + response.status())
            println("out: " + response.content)
            val out = serializer.parse(StringMirror, response.content ?: "")
            println("out: " + out)
        }
    }
}