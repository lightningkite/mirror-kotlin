package com.lightningkite.mirror.old

import java.io.Writer

/**
 * Created by josep on 5/10/2017.
 */
class TabWriter(val out: Writer) {

    var tabs = 0

    fun append(string: String) = out.write(string)
    fun flush() = out.flush()
    fun close() = out.close()

    fun writeln(text: String = "") {
        append("\n")
        repeat(tabs) {
            append("\t")
        }
        append(text)
    }

    fun <T> writelnList(list: List<T>, separator: String = ",", prepend: String = "listOf(", suffix: String = ")", howToWrite: TabWriter.(T) -> Unit) {
        writeln(prepend)
        tabs++
        if (list.isNotEmpty()) {
            for (item in list.subList(0, list.lastIndex)) {
                howToWrite(item)
                append(separator)
            }
            howToWrite(list.last())
        }
        tabs--
        writeln(suffix)
    }
}