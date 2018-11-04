package com.lightningkite.mirror.serialization


/**
 * Finds the position an item belongs in the list (assuming it's sorted) according to [compare] and inserts it there.
 */
fun <E> MutableList<E>.addSorted(item: E, compare: (E, E) -> Boolean): Int {
    var index = 0
    for (it in this) {
        if (compare(item, it)) {
            break
        }
        index++
    }
    add(index, item)
    return index
}