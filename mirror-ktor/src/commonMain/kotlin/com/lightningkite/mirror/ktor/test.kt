package com.lightningkite.mirror.ktor

import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readBytes
import io.ktor.client.response.readText
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.TextContent
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

fun SerialFormat.contentType(): ContentType? = when (this) {
    is Json -> ContentType.Application.Json
    is Cbor -> ContentType("application", "cbor")
    is ProtoBuf -> ContentType("application", "protobuf")
    else -> null
}

fun <V> StringFormat.ktorContent(
        serializationStrategy: SerializationStrategy<V>,
        value: V,
        contentType: ContentType = this.contentType()
                ?: throw UnsupportedOperationException("No content type known for format ${this}")
): TextContent {
    return TextContent(
            text = stringify(serializationStrategy, value),
            contentType = contentType
    )
}

fun <V> BinaryFormat.ktorContent(
        serializationStrategy: SerializationStrategy<V>,
        value: V,
        contentType: ContentType = this.contentType()
                ?: throw UnsupportedOperationException("No content type known for format ${this}")
): ByteArrayContent {
    return ByteArrayContent(
            bytes = dump(serializationStrategy, value),
            contentType = contentType
    )
}

suspend fun <V> StringFormat.ktorResponse(deserializer: DeserializationStrategy<V>, response: HttpResponse): V {
    return parse(deserializer, response.readText())
}

suspend fun <V> BinaryFormat.ktorResponse(deserializer: DeserializationStrategy<V>, response: HttpResponse): V {
    return load(deserializer, response.readBytes())
}

