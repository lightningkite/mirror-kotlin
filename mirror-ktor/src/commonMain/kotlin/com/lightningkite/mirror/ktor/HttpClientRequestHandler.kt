package com.lightningkite.mirror.ktor

import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.request.*
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.SerialFormat

class HttpClientRequestHandler(
        val client: HttpClient,
        val url: String,
        val serializer: SerialFormat,
        val contentType: ContentType = serializer.contentTypeOrFail()
) : Request.Handler {
    override suspend fun <T> invoke(request: Request<T>): T {
        val result = client.call(url) {
            method = HttpMethod.Post
            @Suppress("UNCHECKED_CAST")
            body = serializer.ktorContent(
                    serializationStrategy = RequestMirror.minimal as RequestMirror<T>,
                    value = request,
                    contentType = contentType
            )
            accept(contentType)
        }
        @Suppress("UNCHECKED_CAST") val raw = serializer.ktorResponse(
                deserializer = RemoteResultMirror(request.returnType) as RemoteResultMirror<T>,
                response = result.response
        )
        return raw.result
    }
}