package com.lightningkite.mirror

import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.MirrorType
import com.lightningkite.mirror.ktor.contentTypeOrFail
import io.ktor.application.ApplicationCall
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.content.TextContent
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentType
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat

class MirrorStringSerializerConverter(
        val format: StringFormat
) : ContentConverter {
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request: ApplicationReceiveRequest = context.subject
        val type = context.context.receiveMirrorType ?: MirrorRegistry[request.type] ?: return null
        val text = request.value.let { it as ByteReadChannel }.toInputStream().reader(context.context.request.contentType().charset()
                ?: Charsets.UTF_8).readText()
        return format.parse(type, text)
    }

    override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
        if (value is StrictlyTypedWrapper<*>) {
            @Suppress("UNCHECKED_CAST")
            val encoded = format.stringify(value.type as MirrorType<Any?>, value.value)
            return TextContent(
                    text = encoded,
                    contentType = contentType
            )
        } else {
            val type = MirrorRegistry.retrieveOrNull(value) ?: return null
            val encoded = format.stringify(type as MirrorType<Any?>, value)
            @Suppress("UNCHECKED_CAST")
            return TextContent(
                    text = encoded,
                    contentType = contentType
            )
        }

    }
}

fun ContentNegotiation.Configuration.register(format: StringFormat, contentType: ContentType = format.contentTypeOrFail()){
    register(contentType, MirrorStringSerializerConverter(format))
}