package com.lightningkite.mirror.serialization.json

import com.lightningkite.mirror.string.CharIteratorReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReaderTest {
    @Test
    fun backTest() {
        val testString = "abcdefghijklmnopqrstuv"
        val reader = CharIteratorReader(testString.iterator())
        assertEquals(testString.substring(0, 3), reader.read(3))
        assertEquals(testString[3], reader.peek())
        assertEquals(testString[3], reader.peek())
        assertTrue { reader.check(testString[3]) }
        assertTrue { reader.check(testString.substring(3, 7)) }
        assertEquals(testString.substring(3, 6), reader.read(3))
    }

    @Test
    fun whitespaceTest() {
        val testString = "a  \t  \n  b"
        val reader = CharIteratorReader(testString.iterator())
        assertEquals("a", reader.readWhile { !it.isWhitespace() })
        reader.skipWhitespace()
        assertTrue { reader.check('b') }
    }
}