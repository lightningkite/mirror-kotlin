package com.lightningkite.mirror.info

import com.lightningkite.kommon.string.Uri
import com.lightningkite.kommon.thread.ThreadLocal
import kotlinx.io.ByteArrayInputStream
import kotlinx.io.InputStream
import kotlinx.serialization.*
import kotlin.reflect.KClass

/**
 * This interface is specifically designed to send/receive large amounts of data on a side-channel while serializing/deserializing data.
 * Using this, you can make RPC requests and send up or receive files.
 * It is recommended that servers tweak these values to send down a specific implementation that requests HTTP instead, rather than using Multipart.
 */
interface Data {
    fun read(): InputStream

    companion object {
        val empty = object : Data {
            override fun read(): InputStream = ByteArrayInputStream(byteArrayOf())
        }
    }
}

object DataMirror : MirrorClass<Data>() {
    override val empty: Data
        get() = Data.empty
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf()
    override val kClass: KClass<Data> get() = Data::class
    override val packageName: String get() = "com.lightningkite.mirror.stream"
    override val localName: String get() = "Data"
    override val fields: Array<Field<Data, *>> get() = arrayOf()
    override val kind: SerialKind get() = PrimitiveKind.INT
    override val companion: Any? get() = Data
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    override fun deserialize(decoder: Decoder): Data = serializationDataScope.generate(decoder.decodeInt())
    override fun serialize(encoder: Encoder, obj: Data) = encoder.encodeInt(serializationDataScope.put(obj))
}

@SharedImmutable
val serializationDataScope by ThreadLocal(SerializationDataScope())

class SerializationDataScope {
    val list = ArrayList<Data>()
    private var depth: Int = 0

    fun start() {
        if (depth == 0) {
            list.clear()
        }
        depth++
    }

    fun end() {
        depth--
        if (depth == 0) {
            list.clear()
        }
    }

    fun put(data: Data): Int {
        list.add(data)
        return list.lastIndex
    }

    operator fun get(index: Int): Data {
        return list[index]
    }

    var generateParsedDataForIndex: (Int) -> InputStream = { Data.empty.read() }
    fun generate(index: Int): Data = object : Data {
        val myGetter = generateParsedDataForIndex
        val myIndex = index
        override fun read(): InputStream = myGetter(myIndex)
    }
}

inline fun writeDataScope(run: () -> Unit): List<Data> {
    val scope = serializationDataScope
    scope.start()
    val datas: List<Data> = try {
        run()
        scope.list.toList()
    } finally {
        scope.end()
    }
    return datas
}

inline fun readDataScope(datas: List<Data>, run: () -> Unit) {
    val scope = serializationDataScope
    scope.start()
    scope.generateParsedDataForIndex = {
        datas[it].read()
    }
    try {
        run()
    } finally {
        scope.end()
    }
}