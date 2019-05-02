package com.lightningkite.mirror.archive.model

import com.lightningkite.mirror.info.MirrorClass

interface Sort<T> : Comparator<T> {
    fun iterable(): Iterable<Sort<T>> = listOf()
    fun after(item: T): Condition<T>
    fun equal(item: T): Condition<T>

    data class Multi<T>(val comparators: List<Sort<T>>) : Sort<T> {
        override fun compare(a: T, b: T): Int {
            for (comparator in comparators) {
                val result = comparator.compare(a, b)
                if (result != 0)
                    return result
            }
            return 0
        }

        override fun iterable(): Iterable<Sort<T>> = comparators

        override fun after(item: T): Condition<T> {
            return Condition.Or(comparators.mapIndexed { index, sort ->
                Condition.And(comparators.subList(0, index).map { it.equal(item) } + sort.after(item))
            }).simplify()
        }

        override fun equal(item: T): Condition<T> {
            return Condition.And(comparators.map { it.equal(item) })
        }
    }

    class Natural<T : Comparable<T>> : Sort<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }

        override fun after(item: T): Condition<T> {
            return Condition.GreaterThan(item)
        }

        override fun equal(item: T): Condition<T> {
            return Condition.Equal(item)
        }
    }

    data class Field<T : Any, V : Comparable<V>>(
            val field: MirrorClass.Field<T, V>,
            val ascending: Boolean = true
    ) : Sort<T> {
        override fun compare(a: T, b: T): Int {
            val aValue = field.get(a)
            val bValue = field.get(b)
            return aValue.compareTo(bValue)
        }

        override fun after(item: T): Condition<T> {
            return Condition.Field(field, if (ascending) {
                Condition.GreaterThan(field.get(item))
            } else {
                Condition.LessThan(field.get(item))
            })
        }

        override fun equal(item: T): Condition<T> {
            return Condition.Field(field, Condition.Equal(field.get(item)))
        }
    }

}