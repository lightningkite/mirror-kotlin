package com.lightningkite.mirror.server.test

import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.mirror.MirrorStringSerializerConverter
import com.lightningkite.mirror.PrincipalWrapper
import com.lightningkite.mirror.info.*
import com.lightningkite.mirror.expose
import com.lightningkite.mirror.request.RemoteExceptionData
import com.lightningkite.mirror.request.Request
import com.lightningkite.mirror.request.LocalRequestHandler
import com.lightningkite.mirror.request.RequestMirror
import com.lightningkite.mirror.request.RemoteResultMirror
import com.lightningkite.mirror.request.registerRequests
import com.lightningkite.mirror.ktor.HttpClientRequestHandler
import com.lightningkite.mirror.server.registerMirrorServerTest
import io.ktor.application.Application
import kotlinx.serialization.json.Json
import org.junit.Test
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.client.HttpClient
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
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit

class KtorBasicsTest {
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
            val converter = MirrorStringSerializerConverter(serializer, ContentType.Application.Json)
            register(converter.contentType, converter)
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

    var server: ApplicationEngine? = null

    @KtorExperimentalAPI
    @Before
    fun before() {
        server = embeddedServer(CIO, port = 8080) {
            setupTestApplication()
        }.start(false)
        Thread.sleep(100L)
    }

    @After
    fun after() {
        server?.stop(0L, 100L, TimeUnit.MILLISECONDS)
    }

    val requestHandler = HttpClientRequestHandler(
            client = HttpClient { },
            url = "http://localhost:8080/request",
            serializer = serializer
    )

    @Test
    fun throwing() {
        runBlocking {
            var exceptionOccurred = false
            try {
                requestHandler.invoke(ThrowExceptionRequest())
            } catch (e: RemoteExceptionData.Thrown) {
                assert(e.type == "ForbiddenException")
                exceptionOccurred = true
            }
            assert(exceptionOccurred)
        }
    }

    @Test
    fun pinging(){
        runBlocking {
            val result = requestHandler.invoke(PingRequest("Joseph"))
            assert(result == "Hello, Joseph!")
        }
    }
}