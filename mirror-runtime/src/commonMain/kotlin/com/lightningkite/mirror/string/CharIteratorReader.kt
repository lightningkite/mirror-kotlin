package com.lightningkite.mirror.string

class CharIteratorReader(source: CharIterator) : CharIterator() {
    var position: Int = -1
    val iter = source
    val stack = CharArray(1024)
    var stackPointer = 0

    override fun hasNext(): Boolean = stackPointer != 0 || iter.hasNext()
    override fun nextChar(): Char {
        position++
        return if (stackPointer == 0) iter.nextChar() else stack[--stackPointer]
    }

    fun skip() {
        if (hasNext())
            nextChar()
    }

    fun back(char: Char) {
        position--
        stack[stackPointer++] = char
    }

    fun back(string: String) {
        for (c in string.reversed()) {
            back(c)
        }
    }


    fun peek(): Char {
        val r = nextChar()
        back(r)
        return r
    }

    fun peekWhile(condition: (Char) -> Boolean): String {
        val builder = StringBuilder()
        while (hasNext()) {
            val c = nextChar()
            if (condition(c)) {
                builder.append(c)
            } else {
                val result = builder.toString()
                back(result)
                return result
            }
        }
        val result = builder.toString()
        back(result)
        return result
    }


    fun check(char: Char) = peek() == char

    fun check(string: String): Boolean {
        for (index in string.indices) {
            if (!hasNext()) return false
            val current = nextChar()
            if (current != string[index]) {
                back(current)
                back(string.substring(0, index))
                return false
            }
        }
        back(string)
        return true
    }

    fun checkIgnoreCase(string: String): Boolean {
        for (index in string.indices) {
            if (!hasNext()) return false
            val current = nextChar()
            if (current.equals(string[index], true)) {
                back(current)
                back(string.substring(0, index))
                return false
            }
        }
        back(string)
        return true
    }

    fun checkAndMove(char: Char): Boolean {
        val r = nextChar()
        return if (r != char) {
            back(r)
            false
        } else true
    }

    fun checkAndMove(string: String): Boolean {
        for (index in string.indices) {
            if (!hasNext()) return false
            val current = nextChar()
            if (current != string[index]) {
                back(current)
                back(string.substring(0, index))
                return false
            }
        }
        return true
    }

    fun checkAndMoveIgnoreCase(string: String): Boolean {
        for (index in string.indices) {
            if (!hasNext()) return false
            val current = nextChar()
            if (!current.equals(string[index], true)) {
                back(current)
                back(string.substring(0, index))
                return false
            }
        }
        return true
    }


    fun read(count: Int) = buildString {
        repeat(count) {
            if (!hasNext()) throw Exception("Not enough characters")
            append(nextChar())
        }
    }

    inline fun readWhile(condition: (Char) -> Boolean): String {
        val builder = StringBuilder()
        while (hasNext()) {
            val c = nextChar()
            if (condition(c)) {
                builder.append(c)
            } else {
                back(c)
                val result = builder.toString()
                return result
            }
        }
        val result = builder.toString()
        return result
    }


    inline fun skip(condition: (Char) -> Boolean) {
        while (hasNext()) {
            val c = nextChar()
            if (!condition(c)) {
                back(c)
                break
            }
        }
    }

    inline fun skip(count: Int) {
        repeat(count) {
            if (hasNext())
                nextChar()
        }
    }

    fun skipWhitespace() = skip { it.isWhitespace() }

    fun skipAssert(string: String) {
        val read = read(string.length)
        if (read != string)
            throw Exception("Expected '$string', got $read")
    }
}