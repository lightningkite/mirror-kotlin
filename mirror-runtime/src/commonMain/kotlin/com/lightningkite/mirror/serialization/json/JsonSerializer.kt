package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.info.Type
import com.lightningkite.mirror.info.info
import com.lightningkite.mirror.info.type
import com.lightningkite.mirror.info.untyped
import com.lightningkite.mirror.serialization.*
import com.lightningkite.mirror.string.CharIteratorReader
import kotlin.reflect.KClass

@Suppress("LeakingThis")
open class JsonSerializer : StringSerializer, Encoder<Appendable>, Decoder<CharIteratorReader> {

    companion object : JsonSerializer()

    override val arbitraryEncoders: MutableList<Encoder.Generator<Appendable>> = ArrayList()
    override val kClassEncoders: MutableMap<KClass<*>, (Type<*>) -> (Appendable.(value: Any?) -> Unit)?> = HashMap()
    override val kClassDecoders: MutableMap<KClass<*>, (Type<*>) -> (CharIteratorReader.() -> Any?)?> = HashMap()
    override val encoders: MutableMap<Type<*>, Appendable.(value: Any?) -> Unit> = HashMap()
    override val arbitraryDecoders: MutableList<Decoder.Generator<CharIteratorReader>> = ArrayList()
    override val decoders: MutableMap<Type<*>, CharIteratorReader.() -> Any?> = HashMap()

    override val contentType: String = "application/json"

    override fun <V : Any> write(value: V?, type: Type<V>): String {
        val builder = StringBuilder()
        encoder(type).invoke(builder, value)
        return builder.toString()
    }

    override fun <V : Any> read(from: String, type: Type<V>): V? {
        val reader = CharIteratorReader(from.iterator())
        return decoder(type).invoke(reader)
    }

    fun CharIteratorReader.decodeNumber(): Double {
        return readWhile {
            when (it) {
                in '0'..'9',
                '-',
                '.',
                'e',
                'E',
                '+' -> true
                else -> false
            }
        }.toDouble()
    }

    init {

        addEncoder(Unit::class.type) {
            append('0')
        }
        addDecoder(Unit::class.type) {
            skip(1)
        }

        addEncoder(Boolean::class.type) {
            append(it.toString())
        }
        addDecoder(Boolean::class.type) {
            when (peek()) {
                't', 'T' -> {
                    checkIgnoreCase("true")
                    true
                }
                'f', 'F' -> {
                    checkIgnoreCase("false")
                    true
                }
                else -> throw SerializationException("Invalid boolean")
            }
        }

        addEncoder(Byte::class.type) {
            append(it.toString())
        }
        addDecoder(Byte::class.type) {
            decodeNumber().toByte()
        }

        addEncoder(Short::class.type) {
            append(it.toString())
        }
        addDecoder(Short::class.type) {
            decodeNumber().toShort()
        }

        addEncoder(Int::class.type) {
            append(it.toString())
        }
        addDecoder(Int::class.type) {
            decodeNumber().toInt()
        }

        addEncoder(Long::class.type) {
            append(it.toString())
        }
        addDecoder(Long::class.type) {
            decodeNumber().toLong()
        }

        addEncoder(Float::class.type) {
            append(it.toString())
        }
        addDecoder(Float::class.type) {
            decodeNumber().toFloat()
        }

        addEncoder(Double::class.type) {
            append(it.toString())
        }
        addDecoder(Double::class.type) {
            decodeNumber()
        }

        addEncoder(String::class.type) { string ->
            append('\"')
            for (ch in string!!) {
                when (ch) {
                    '"' -> append("\\").append(ch)
                    '\\' -> append(ch).append(ch)
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    '\b' -> append("\\b")
                    '\u000c' -> append("\\f")
                    in '\u0000'..'\u001F',
                    in '\u007F'..'\u009F',
                    in '\u2000'..'\u20FF' -> {
                        append("\\u")
                        append(ch.toInt().toString(16).padStart(4, '0'))
                    }
                    else -> append(ch)
                }
            }
            append('\"')
        }
        addDecoder(String::class.type) {
            skipAssert("\"")
            var escaped = false
            val builder = StringBuilder()
            loop@ while (hasNext()) {
                val it = nextChar()
                if (escaped) {
                    when (it) {
                        '"' -> builder.append('\"')
                        '\\' -> builder.append('\\')
                        'n' -> builder.append('\n')
                        'r' -> builder.append('\r')
                        't' -> builder.append('\t')
                        'b' -> builder.append('\b')
                        'f' -> builder.append('\u000c')
                        'u' -> {
                            read(4).toInt(16)
                        }
                        else -> builder.append(it)
                    }
                } else {
                    when (it) {
                        '\\' -> escaped = true
                        '"' -> break@loop
                        else -> builder.append(it)
                    }
                }
            }
            builder.toString()
        }

        val stringEncoder = encoder(String::class.type)
        addEncoder(Char::class.type) {
            stringEncoder(this, it.toString())
        }
        val stringDecoder = decoder(String::class.type)
        addDecoder(Char::class.type) {
            stringDecoder(this)!!.first()
        }

        setNotNullEncoder(List::class) { type ->
            val subEncoder = rawEncoder(type.param(0).type)
            return@setNotNullEncoder { value ->
                append('[')
                var first = true
                for (sub in value as List<*>) {
                    if (first) {
                        first = false
                    } else {
                        append(',')
                    }
                    subEncoder.invoke(this, sub)
                }
                append(']')
            }
        }
        setNotNullDecoder(List::class) { type ->
            val subDecoder = rawDecoder(type.param(0).type)
            return@setNotNullDecoder {
                val list = ArrayList<Any?>()
                skipAssert("[")
                while (hasNext()) {
                    if (peek() == ']') break
                    skipWhitespace()
                    list.add(subDecoder.invoke(this))
                    skipWhitespace()
                    if (peek() == ']') break
                    else skipAssert(",")
                }
                skipAssert("]")
                list
            }
        }

        setNotNullEncoder(Map::class) { type ->
            if (type.param(0).type.let { it.kClass == String::class && !it.nullable }) {
                val subEncoder = rawEncoder(type.param(1).type)
                return@setNotNullEncoder { value ->
                    append('{')
                    var first = true
                    for ((key, sub) in value as Map<*, *>) {
                        if (first) {
                            first = false
                        } else {
                            append(',')
                        }
                        stringEncoder.invoke(this, key as String)
                        append(':')
                        subEncoder.invoke(this, sub)
                    }
                    append('}')
                }
            } else {
                val keyEncoder = rawEncoder(type.param(0).type)
                val subEncoder = rawEncoder(type.param(1).type)
                return@setNotNullEncoder { value ->
                    append('{')
                    var first = true
                    for ((key, sub) in value as Map<*, *>) {
                        if (first) {
                            first = false
                        } else {
                            append(',')
                        }
                        keyEncoder.invoke(this, key)
                        append(':')
                        subEncoder.invoke(this, sub)
                    }
                    append('}')
                }
            }
        }
        setNotNullDecoder(Map::class) { type ->
            if (type.param(0).type.let { it.kClass == String::class && !it.nullable }) {
                val subDecoder = rawDecoder(type.param(1).type)
                return@setNotNullDecoder {
                    val map = LinkedHashMap<Any?, Any?>()
                    skipAssert("{")
                    while (hasNext()) {
                        if (peek() == '}') break
                        skipWhitespace()
                        val key = stringDecoder.invoke(this)
                        skipWhitespace()
                        skipAssert(":")
                        skipWhitespace()
                        val value = subDecoder.invoke(this)
                        map[key] = value
                        skipWhitespace()
                        if (peek() == '}') break
                        else skipAssert(",")
                    }
                    skipAssert("}")
                    map
                }
            } else {
                val keyDecoder = rawDecoder(type.param(0).type)
                val subDecoder = rawDecoder(type.param(1).type)
                return@setNotNullDecoder {
                    val map = LinkedHashMap<Any?, Any?>()
                    skipAssert("{")
                    while (hasNext()) {
                        if (peek() == '}') break
                        skipWhitespace()
                        val key = keyDecoder.invoke(this)
                        skipWhitespace()
                        skipAssert(":")
                        skipWhitespace()
                        val value = subDecoder.invoke(this)
                        map[key] = value
                        skipWhitespace()
                        if (peek() == '}') break
                        else skipAssert(",")
                    }
                    skipAssert("}")
                    map
                }
            }
        }

        addDecoder(EnumCoderGenerators.DecoderGenerator(this))
        addEncoder(EnumCoderGenerators.EncoderGenerator(this))
        addDecoder(ReflectiveDecoderGenerator())
        addEncoder(ReflectiveEncoderGenerator())
        addDecoder(PolymorphicDecoderGenerator())
        addEncoder(PolymorphicEncoderGenerator())
        addDecoder(NullableDecoderGenerator())
        addEncoder(NullableEncoderGenerator())
    }

    inner class ReflectiveEncoderGenerator : Encoder.Generator<Appendable> {
        override val priority: Float get() = 0f

        override fun generateEncoder(type: Type<*>): (Appendable.(value: Any?) -> Unit)? {

            if (type.nullable) return null
            val stringEncoder = rawEncoder(String::class.type)
            val lazySubCoders by lazy { type.kClass.info.fields.associateWith { rawEncoder(it.type as Type<*>) } }

            return { it ->
                this.append('{')
                var first = true
                for ((key, sub) in lazySubCoders) {
                    if (first) {
                        first = false
                    } else {
                        this.append(',')
                    }
                    stringEncoder.invoke(this, key.name)
                    this.append(':')
                    sub.invoke(this, key.get.untyped(it!!))
                }
                this.append('}')
            }
        }
    }

    fun CharIteratorReader.skipValue() {
        when (peek()) {
            in '0'..'9',
            '-',
            '.',
            'e',
            'E',
            '+' -> decodeNumber()
            'f', 'F' -> skip(5)
            't', 'T' -> skip(4)
            '[' -> {
                var counter = 1
                skip {
                    when (it) {
                        '[' -> {
                            counter++
                            true
                        }
                        ']' -> {
                            counter--
                            counter > 0
                        }
                        else -> true
                    }
                }
            }
            '{' -> {
                var counter = 1
                skip {
                    when (it) {
                        '{' -> {
                            counter++
                            true
                        }
                        '}' -> {
                            counter--
                            counter > 0
                        }
                        else -> true
                    }
                }
            }
        }
    }

    inner class ReflectiveDecoderGenerator : Decoder.Generator<CharIteratorReader> {
        override val priority: Float get() = 0f

        override fun generateDecoder(type: Type<*>): (CharIteratorReader.() -> Any?)? {
            if (type.nullable) return null
            val stringDecoder = decoder(String::class.type)
            val fields = type.kClass.info.fields
            val subCoders by lazy { fields.associate { it.name to rawDecoder(it.type as Type<*>) } }

            return {
                val map = HashMap<String, Any?>()
                skipAssert("{")
                while (hasNext()) {
                    if (peek() == '}') break
                    skipWhitespace()
                    val key = stringDecoder.invoke(this)!!
                    skipWhitespace()
                    skipAssert(":")
                    skipWhitespace()
                    val subCoder = subCoders[key]
                    if (subCoder == null) {
                        skipValue()
                    } else {
                        val value = subCoder.invoke(this)
                        map[key] = value
                    }
                    skipWhitespace()
                    if (peek() == '}') break
                    else skipAssert(",")
                }
                skipAssert("}")

                type.kClass.info.construct(map)
            }
        }
    }

    inner class PolymorphicEncoderGenerator : Encoder.Generator<Appendable> {
        override val priority: Float get() = .1f

        override fun generateEncoder(type: Type<*>): (Appendable.(value: Any?) -> Unit)? {
            if (!type.kClass.serializePolymorphic) return null
            val string = rawEncoder(String::class.type)
            return { value ->
                val underlyingType = when (value) {
                    is List<*> -> List::class
                    is Map<*, *> -> Map::class
                    else -> value!!::class
                }
                append('[')
                string.invoke(this, underlyingType.externalName)
                append(',')
                rawEncoder(underlyingType.type).invoke(this, value)
                append(']')
            }
        }
    }

    inner class PolymorphicDecoderGenerator : Decoder.Generator<CharIteratorReader> {
        override val priority: Float get() = .1f

        override fun generateDecoder(type: Type<*>): (CharIteratorReader.() -> Any?)? {
            if (!type.kClass.serializePolymorphic) return null
            val string = rawDecoder(String::class.type)
            return {
                skipAssert("[")
                skipWhitespace()
                val actualType = KClassesByExternalName[string.invoke(this)]!!
                skipWhitespace()
                skipAssert(",")
                skipWhitespace()
                val value = rawDecoder(actualType.type).invoke(this)
                skipAssert("]")
                skipWhitespace()
                value
            }
        }
    }

    inner class NullableEncoderGenerator : Encoder.Generator<Appendable> {
        override val priority: Float get() = 1f

        override fun generateEncoder(type: Type<*>): (Appendable.(value: Any?) -> Unit)? {
            if (!type.nullable) return null
            val underlying = rawEncoder(type.copy(nullable = false))
            return { value ->
                if (value == null) {
                    append("null")
                } else {
                    underlying.invoke(this, value)
                }
            }
        }
    }

    inner class NullableDecoderGenerator : Decoder.Generator<CharIteratorReader> {
        override val priority: Float get() = 1f

        override fun generateDecoder(type: Type<*>): (CharIteratorReader.() -> Any?)? {
            if (!type.nullable) return null
            val underlying = rawDecoder(type.copy(nullable = false))
            return {
                if (checkAndMoveIgnoreCase("null")) {
                    null
                } else underlying.invoke(this)
            }
        }
    }
}