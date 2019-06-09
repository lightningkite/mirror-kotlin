package com.lightningkite.mirror.ktor

import com.lightningkite.kommon.atomic.AtomicLong
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.mirror.info.AnyMirror
import com.lightningkite.mirror.info.MirrorRegistry
import com.lightningkite.mirror.info.list
import com.lightningkite.mirror.request.*
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.*
import kotlinx.serialization.SerialFormat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpClientMultiRequestHandler(
        val client: HttpClient,
        val url: String,
        val serializer: SerialFormat,
        val contentType: ContentType = serializer.contentTypeOrFail(),
        val delayMilliseconds: Long = 100
) : Request.Handler {

    val hits = AtomicLong(0)

    private suspend fun sendGroup(list: List<Request<*>>): List<RemoteResult<Any?>> {
        hits.increment()
        val result = client.call(url) {
            method = HttpMethod.Post
            @Suppress("UNCHECKED_CAST")
            body = serializer.ktorContent(
                    serializationStrategy = RequestMirror.minimal.list,
                    value = list,
                    contentType = contentType
            )
            accept(contentType)
        }
        @Suppress("UNCHECKED_CAST") val raw = serializer.ktorResponse(
                deserializer = RemoteResultMirror(AnyMirror.nullable).list,
                response = result.response
        )
        return raw
    }

    data class QueuedRequest(
            val id: Long = 0,
            val request: Request<*>,
            val callback: (RemoteResult<*>) -> Unit
    )

    val currentList = AtomicReference<List<QueuedRequest>>(listOf())
    val requestNumber = AtomicLong(0)

    override suspend fun <T> invoke(request: Request<T>): T {
        return suspendCoroutine<T> { continuation ->
            GlobalScope.launch {
                val myRequestNumber = requestNumber.addAndGet(1)
                var current = currentList.value
                while (!currentList.compareAndSet(current, current + QueuedRequest(
                                id = myRequestNumber,
                                request = request,
                                callback = {
                                    if(it.success){
                                        continuation.resume(it.result as T)
                                    }else {
                                        continuation.resumeWithException(RemoteExceptionData.Thrown(it.exception!!))
                                    }
                                }
                        ))) {
                    delay(1)
                    current = currentList.value
                }
                if (current.isEmpty()) {
                    //I start it
                    delay(delayMilliseconds)
                    current = currentList.value
                    while (!currentList.compareAndSet(current, listOf())) {
                        delay(1)
                        current = currentList.value
                    }
                    val ordered = current.sortedBy { it.id }
                    val results = sendGroup(ordered.map { it.request })
                    results.asSequence().zip(current.asSequence()).forEach {
                        it.second.callback.invoke(it.first)
                    }
                }
                //Otherwise just wait
            }
        }

    }
}