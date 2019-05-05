package com.lightningkite.mirror.breaker

/*
 * Copyright 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.lightningkite.mirror.info.MirrorEnum
import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

open class PartialBreaker(context: SerialModule = EmptyModule) : AbstractSerialFormat(context) {

    companion object : PartialBreaker()

    fun <T> fold(type: KSerializer<T>, elements: Map<Int, Any?>) = type.deserialize(D(elements))

    inner class D(val elements: Map<Int, Any?>) : Decoder {
        override val context: SerialModule get() = this@PartialBreaker.context
        override val updateMode: UpdateMode get() = UpdateMode.OVERWRITE

        override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder = DE(elements)
        override fun decodeBoolean(): Boolean = elements[0] as Boolean
        override fun decodeByte(): Byte = elements[0] as Byte
        override fun decodeChar(): Char = elements[0] as Char
        override fun decodeDouble(): Double = elements[0] as Double
        override fun decodeEnum(enumDescription: EnumDescriptor): Int = elements[0] as Int
        override fun decodeFloat(): Float = elements[0] as Float
        override fun decodeInt(): Int = elements[0] as Int
        override fun decodeLong(): Long = elements[0] as Long
        override fun decodeNotNullMark(): Boolean = elements[0] != null
        override fun decodeShort(): Short = elements[0] as Short
        override fun decodeString(): String = elements[0] as String
        override fun decodeNull(): Nothing? = null
        override fun decodeUnit() = Unit
    }

    inner class DE(val elements: Map<Int, Any?>) : CompositeDecoder {
        override val context: SerialModule get() = this@PartialBreaker.context
        override val updateMode: UpdateMode get() = UpdateMode.OVERWRITE

        override fun decodeBooleanElement(desc: SerialDescriptor, index: Int): Boolean {
            return elements[index] as Boolean
        }

        override fun decodeByteElement(desc: SerialDescriptor, index: Int): Byte {
            return elements[index] as Byte
        }

        override fun decodeCharElement(desc: SerialDescriptor, index: Int): Char {
            return elements[index] as Char
        }

        override fun decodeDoubleElement(desc: SerialDescriptor, index: Int): Double {
            return elements[index] as Double
        }

        override fun decodeFloatElement(desc: SerialDescriptor, index: Int): Float {
            return elements[index] as Float
        }

        override fun decodeIntElement(desc: SerialDescriptor, index: Int): Int {
            return elements[index] as Int
        }

        override fun decodeLongElement(desc: SerialDescriptor, index: Int): Long {
            return elements[index] as Long
        }

        override fun <T : Any> decodeNullableSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>): T? {
            return elements[index] as T?
        }

        override fun <T> decodeSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>): T {
            return elements[index] as T
        }

        override fun decodeShortElement(desc: SerialDescriptor, index: Int): Short {
            return elements[index] as Short
        }

        override fun decodeStringElement(desc: SerialDescriptor, index: Int): String {
            return elements[index] as String
        }

        override fun decodeUnitElement(desc: SerialDescriptor, index: Int) {
            return elements[index] as Unit
        }

        override fun <T : Any> updateNullableSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, old: T?): T? {
            return elements[index] as T?
        }

        override fun <T> updateSerializableElement(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, old: T): T {
            return elements[index] as T
        }

        private val indexIterator = elements.keys.iterator()
        override fun decodeElementIndex(desc: SerialDescriptor): Int = if (indexIterator.hasNext()) indexIterator.next() else CompositeDecoder.READ_DONE

    }


}
