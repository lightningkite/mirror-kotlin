package com.lightningkite.mirror.fastjson

import com.lightningkite.kommon.string.CharIteratorReader
import kotlinx.serialization.*
import kotlinx.serialization.context.SerialContext
import kotlinx.serialization.internal.EnumDescriptor

object LameJson : AbstractSerialFormat(), StringFormat {

    const val MODE_LIST = 0
    const val MODE_MAP = 1
    const val MODE_CLASS = 2

    class Encooder(override val context: SerialContext, val builder: StringBuilder) : Encoder, CompositeEncoder {

        val modeStack = IntArray(256)
        val commaNeeded = BooleanArray(256)
        var modeStackPointer = -1

        override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeEncoder {
            when (desc.kind) {
                is StructureKind.LIST -> {
                    modeStackPointer++
                    modeStack[modeStackPointer] = MODE_LIST
                    builder.append("[")
                }
                is StructureKind.MAP -> {
                    modeStackPointer++
                    modeStack[modeStackPointer] = 1
                    builder.append("{")
                }
                else -> {
                    modeStackPointer++
                    modeStack[modeStackPointer] = 2
                    builder.append("{")
                }
            }
            return this
        }

        override fun endStructure(desc: SerialDescriptor) {
            when (modeStack[modeStackPointer]) {
                MODE_LIST -> {
                    modeStackPointer--
                    builder.append("]")
                }
                MODE_MAP -> {
                    modeStackPointer--
                    builder.append("}")
                }
                MODE_CLASS -> {
                    modeStackPointer--
                    builder.append("}")
                }
            }
        }

        override fun encodeBoolean(value: Boolean) {
            builder.append(value.toString())
        }

        override fun encodeByte(value: Byte) {
            builder.append(value.toString())
        }

        override fun encodeDouble(value: Double) {
            builder.append(value.toString())
        }

        override fun encodeFloat(value: Float) {
            builder.append(value.toString())
        }

        override fun encodeInt(value: Int) {
            builder.append(value.toString())
        }

        override fun encodeLong(value: Long) {
            builder.append(value.toString())
        }

        override fun encodeShort(value: Short) {
            builder.append(value.toString())
        }

        override fun encodeEnum(enumDescription: EnumDescriptor, ordinal: Int) {
            builder.append("\"" + enumDescription.getElementName(ordinal) + "\"")
        }

        override fun encodeNull() {
            builder.append("null")
        }

        override fun encodeUnit() {
            builder.append("{}")
        }

        override fun encodeChar(value: Char) {
            encodeString(value.toString())
        }

        override fun encodeString(value: String) {
            builder.append('"')
            for (ch in value) {
                when (ch) {
                    '"' -> builder.append("\\").append(ch)
                    '\\' -> builder.append(ch).append(ch)
                    '\n' -> builder.append("\\n")
                    '\r' -> builder.append("\\r")
                    '\t' -> builder.append("\\t")
                    '\b' -> builder.append("\\b")
                    '\u000c' -> builder.append("\\f")
                    in '\u0000'..'\u001F',
                    in '\u007F'..'\u009F',
                    in '\u2000'..'\u20FF' -> {
                        builder.append("\\u")
                        builder.append(ch.toInt().toString(16).padStart(4, '0'))
                    }
                    else -> builder.append(ch)
                }
            }
            builder.append('"')
        }

        override fun encodeNotNullMark() {}

        override fun encodeBooleanElement(desc: SerialDescriptor, index: Int, value: Boolean) {
            encodeXElement(desc, index) { encodeBoolean(value) }
        }

        override fun encodeByteElement(desc: SerialDescriptor, index: Int, value: Byte) {
            encodeXElement(desc, index) { encodeByte(value) }
        }

        override fun encodeCharElement(desc: SerialDescriptor, index: Int, value: Char) {
            encodeXElement(desc, index) { encodeChar(value) }
        }

        override fun encodeDoubleElement(desc: SerialDescriptor, index: Int, value: Double) {
            encodeXElement(desc, index) { encodeDouble(value) }
        }

        override fun encodeFloatElement(desc: SerialDescriptor, index: Int, value: Float) {
            encodeXElement(desc, index) { encodeFloat(value) }
        }

        override fun encodeIntElement(desc: SerialDescriptor, index: Int, value: Int) {
            encodeXElement(desc, index) { encodeInt(value) }
        }

        override fun encodeLongElement(desc: SerialDescriptor, index: Int, value: Long) {
            encodeXElement(desc, index) { encodeLong(value) }
        }

        override fun encodeShortElement(desc: SerialDescriptor, index: Int, value: Short) {
            encodeXElement(desc, index) { encodeShort(value) }
        }

        override fun encodeStringElement(desc: SerialDescriptor, index: Int, value: String) {
            encodeXElement(desc, index) { encodeString(value) }
        }

        override fun encodeUnitElement(desc: SerialDescriptor, index: Int) {
            encodeXElement(desc, index) { encodeUnit() }
        }

        inline fun encodeXElement(desc: SerialDescriptor, index: Int, encode: () -> Unit) {
            when (modeStack[modeStackPointer]) {
                MODE_LIST -> {
                    if (commaNeeded[modeStackPointer])
                        builder.append(',')
                    else
                        commaNeeded[modeStackPointer] = true
                    encode()
                }
                MODE_MAP -> {
                    if (index % 2 == 0) {
                        if (commaNeeded[modeStackPointer])
                            builder.append(',')
                        else
                            commaNeeded[modeStackPointer] = true
                        encode()
                    } else {
                        builder.append(':')
                        encode()
                    }
                }
                MODE_CLASS -> {
                    if (commaNeeded[modeStackPointer])
                        builder.append(',')
                    else
                        commaNeeded[modeStackPointer] = true
                    encodeString(desc.getElementName(index))
                    builder.append(':')
                    encode()
                }
            }
        }


        override fun encodeNonSerializableElement(desc: SerialDescriptor, index: Int, value: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun <T : Any> encodeNullableSerializableElement(desc: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
            if (value == null) {
                encodeXElement(desc, index) { encodeNull() }
            } else {
                encodeXElement(desc, index) { serializer.serialize(this, value) }
            }
        }

        override fun <T> encodeSerializableElement(desc: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) {
            encodeXElement(desc, index) { serializer.serialize(this, value) }
        }

    }

    class Decooder(override val context: SerialContext, val reader: CharIteratorReader) : Decoder, CompositeDecoder {

        override val updateMode: UpdateMode
            get() = UpdateMode.UPDATE
        val stack = ArrayList<String>(16)

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

        fun CharIteratorReader.decodeString(): String {
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
                    escaped = false
                } else {
                    when (it) {
                        '\\' -> escaped = true
                        '"' -> break@loop
                        else -> builder.append(it)
                    }
                }
            }
            return builder.toString()
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
                '"' -> decodeString()
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

        override fun decodeBoolean(): Boolean = when (reader.peek()) {
            't', 'T' -> {
                reader.checkAndMoveIgnoreCase("true")
                true
            }
            'f', 'F' -> {
                reader.checkAndMoveIgnoreCase("false")
                true
            }
            else -> throw SerializationException("Invalid boolean")
        }

        override fun decodeByte(): Byte = reader.decodeNumber().toByte()
        override fun decodeDouble(): Double = reader.decodeNumber()
        override fun decodeFloat(): Float = reader.decodeNumber().toFloat()
        override fun decodeInt(): Int = reader.decodeNumber().toInt()
        override fun decodeLong(): Long = reader.decodeNumber().toLong()
        override fun decodeShort(): Short = reader.decodeNumber().toShort()

        override fun decodeString(): String {
            reader.skipAssert("\"")
            var escaped = false
            val builder = StringBuilder()
            loop@ while (reader.hasNext()) {
                val it = reader.nextChar()
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
                            reader.read(4).toInt(16)
                        }
                        else -> builder.append(it)
                    }
                    escaped = false
                } else {
                    when (it) {
                        '\\' -> escaped = true
                        '"' -> break@loop
                        else -> builder.append(it)
                    }
                }
            }
            return builder.toString()
        }

        override fun decodeChar(): Char = decodeString().first()

        override fun decodeEnum(enumDescription: EnumDescriptor): Int {
            return enumDescription.getElementIndex(reader.decodeString())
        }

        override fun decodeNotNullMark(): Boolean = reader.peek() != 'n'

        override fun decodeNull(): Nothing? {
            reader.skipAssert("null")
            return null
        }

        override fun decodeUnit() {
            reader.skipAssert("{}")
        }

        val modeStack = IntArray(256)
        var stackPointer = -1
        val indexStack = IntArray(256) { 0 }

        override fun decodeBooleanElement(desc: SerialDescriptor, index: Int): Boolean = decodeElement(desc, index) { decodeBoolean() }
        override fun decodeByteElement(desc: SerialDescriptor, index: Int): Byte = decodeElement(desc, index) { decodeByte() }
        override fun decodeCharElement(desc: SerialDescriptor, index: Int): Char = decodeElement(desc, index) { decodeChar() }
        override fun decodeDoubleElement(desc: SerialDescriptor, index: Int): Double = decodeElement(desc, index) { decodeDouble() }
        override fun decodeFloatElement(desc: SerialDescriptor, index: Int): Float = decodeElement(desc, index) { decodeFloat() }
        override fun decodeIntElement(desc: SerialDescriptor, index: Int): Int = decodeElement(desc, index) { decodeInt() }
        override fun decodeLongElement(desc: SerialDescriptor, index: Int): Long = decodeElement(desc, index) { decodeLong() }
        override fun decodeShortElement(desc: SerialDescriptor, index: Int): Short = decodeElement(desc, index) { decodeShort() }
        override fun decodeStringElement(desc: SerialDescriptor, index: Int): String = decodeElement(desc, index) { decodeString() }
        override fun decodeUnitElement(desc: SerialDescriptor, index: Int) = decodeElement(desc, index) { decodeUnit() }
        override fun <T : Any?> decodeSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>): T = decodeElement(desc, index) {
            decodeSerializableValue(deserializer)
        }

        override fun <T : Any> decodeNullableSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>): T? = decodeElement(desc, index) {
            decodeNullableSerializableValue(deserializer)
        }

        override fun <T> updateSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, old: T): T = decodeElement(desc, index) {
            updateSerializableValue(deserializer, old)
        }

        override fun <T : Any> updateNullableSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, old: T?): T? = decodeElement(desc, index) {
            updateNullableSerializableValue(deserializer, old)
        }

        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            return when (modeStack[stackPointer]) {
                MODE_LIST -> {
                    reader.skipWhitespace()
                    if (reader.check(']')) {
                        CompositeDecoder.READ_DONE
                    } else {
                        reader.checkAndMove(',')
                        reader.skipWhitespace()
                        indexStack[stackPointer]
                    }
                }
                MODE_MAP -> {
                    reader.skipWhitespace()
                    if (reader.check('}')) {
                        CompositeDecoder.READ_DONE
                    } else {
                        reader.checkAndMove(',')
                        reader.checkAndMove(':')
                        reader.skipWhitespace()
                        indexStack[stackPointer]
                    }
                }
                else -> {
                    while (reader.hasNext()) {
                        reader.skipWhitespace()
                        reader.checkAndMove(',')
                        if (reader.peek() == '}') break
                        reader.skipWhitespace()
                        val key = decodeString()
                        reader.skipWhitespace()
                        reader.skipAssert(":")
                        reader.skipWhitespace()
                        val index = desc.getElementIndex(key)
                        if (index == CompositeDecoder.UNKNOWN_NAME)
                            reader.skipValue()
                        else
                            return index
                    }
                    return CompositeDecoder.READ_DONE
                }
            }
        }

        override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
            return when (desc.kind) {
                StructureKind.LIST -> {
                    stackPointer++
                    modeStack[stackPointer] = 0
                    indexStack[stackPointer] = 0
                    reader.skipAssert("[")
                    this
                }
                StructureKind.MAP -> {
                    stackPointer++
                    modeStack[stackPointer] = 1
                    indexStack[stackPointer] = 0
                    reader.skipAssert("{")
                    this
                }
                else -> {
                    stackPointer++
                    modeStack[stackPointer] = 2
                    indexStack[stackPointer] = 0
                    reader.skipAssert("{")
                    this
                }
            }
        }

        override fun endStructure(desc: SerialDescriptor) {
            when (modeStack[stackPointer]) {
                MODE_LIST -> {
                    reader.skipAssert("]")
                }
                MODE_MAP -> {
                    reader.skipAssert("}")
                }
                else -> {
                    reader.skipAssert("}")
                }
            }
            stackPointer--
        }

        inline fun <T> decodeElement(desc: SerialDescriptor, index: Int, action: () -> T): T {
            val result = action()
            indexStack[stackPointer] = index + 1
            return result
        }
    }

    override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {
        val reader = CharIteratorReader(string.iterator())
        try {
            return Decooder(context, reader).decode(deserializer)
        } catch (e: Exception) {
            throw Exception("Failed to parse at row ${reader.line} column ${reader.column}, string ${string}", e)
        }
    }

    override fun <T> stringify(serializer: SerializationStrategy<T>, obj: T): String {
        return buildString {
            Encooder(context, this).encode(serializer, obj)
        }
    }

}