package com.lightningkite.mirror.source

data class Node(
        val type: String,
        val content: String?,
        val children: List<Node>,
        val terminals: List<String>
) {
    operator fun get(childType: String): Node? = children.find { it.type == childType }
    fun getAll(childType: String): List<Node> = children.filter { it.type == childType }
    operator fun get(childType: String, index: Int): Node? = children.filter { it.type == childType }.getOrNull(index)

    fun print(appendable: Appendable, spaces: Int = 0) {
        repeat(spaces) {
            appendable.append(' ')
        }
        appendable.append(type)
        appendable.append(": ")
        appendable.append(content)
        appendable.append(" | ")
        appendable.append(terminals.joinToString())
        appendable.appendln()
        for (child in children) {
            child.print(appendable, spaces + 1)
        }
    }
}