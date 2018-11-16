package com.lightningkite.kotlinx.persistence

import com.lightningkite.kotlinx.collection.recursiveFlatMap
import com.lightningkite.kotlinx.reflection.ExternalReflection
import com.lightningkite.kotlinx.reflection.KxField

@ExternalReflection
sealed class ConditionOnItem<in T : Any> {

    abstract operator fun invoke(item: T): Boolean
    open fun iterable(): Iterable<ConditionOnItem<T>>? = null
    fun recursing(): Sequence<ConditionOnItem<T>> {
        return sequenceOf(this).recursiveFlatMap { it.iterable()?.asSequence() ?: emptySequence() }
    }

    @ExternalReflection
    interface OnField<T : Any, V> {
        val field: KxField<T, V>
    }

    @ExternalReflection
    class Never<T : Any> : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = false
    }

    @ExternalReflection
    class Always<T : Any> : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = true
    }

    @ExternalReflection
    data class And<T : Any>(val conditions: List<ConditionOnItem<T>>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = conditions.all { it(item) }
        override fun iterable(): Iterable<ConditionOnItem<T>> = conditions
    }

    @ExternalReflection
    data class Or<T : Any>(val conditions: List<ConditionOnItem<T>>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = conditions.any { it(item) }
        override fun iterable(): Iterable<ConditionOnItem<T>> = conditions
    }

    @ExternalReflection
    data class Not<T : Any>(val condition: ConditionOnItem<T>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = !condition(item)
        override fun iterable(): Iterable<ConditionOnItem<T>> = listOf(condition)
    }

    @ExternalReflection
    data class Equal<T : Any, V>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) == value
    }

    @ExternalReflection
    data class EqualToOne<T : Any, V>(override val field: KxField<T, V>, val values: Collection<V>) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) in values
    }

    @ExternalReflection
    data class NotEqual<T : Any, V>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) != value
    }

    @ExternalReflection
    data class LessThan<T : Any, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) < value
    }

    @ExternalReflection
    data class GreaterThan<T : Any, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) > value
    }

    @ExternalReflection
    data class LessThanOrEqual<T : Any, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) <= value
    }

    @ExternalReflection
    data class GreaterThanOrEqual<T : Any, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) >= value
    }

    @ExternalReflection
    data class TextSearch<T : Any, V : CharSequence>(override val field: KxField<T, V>, val query: String) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item).contains(query)
    }

    @ExternalReflection
    data class RegexTextSearch<T : Any, V : CharSequence>(override val field: KxField<T, V>, val query: String) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item).contains(Regex(query))
    }
}
