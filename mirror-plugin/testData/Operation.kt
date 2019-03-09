package com.lightningkite.mirror.archive.model

import com.lightningkite.mirror.info.ClassInfo
import com.lightningkite.mirror.info.MirrorClass
import com.lightningkite.mirror.serialization.toAttributeHashMap

interface Operation<T> {

    companion object {
        fun <T : Any, F> SetField(field: MirrorClass.Field<T, F>, value: F) = Operation.Field(
                field = field,
                operation = Operation.Set(value)
        )
    }

    operator fun invoke(item: T): T

    data class Set<T>(var value: T) : Operation<T> {
        override fun invoke(item: T): T = value
    }

    interface AddNumeric<T> : Operation<T> {
        val amount: Number
    }

    data class AddInt(override var amount: Int) : AddNumeric<Int> {
        override fun invoke(item: Int): Int = item + amount
    }

    data class AddLong(override var amount: Long) : AddNumeric<Long> {
        override fun invoke(item: Long): Long = item + amount
    }

    data class AddFloat(override var amount: Float) : AddNumeric<Float> {
        override fun invoke(item: Float): Float = item + amount
    }

    data class AddDouble(override var amount: Double) : AddNumeric<Double> {
        override fun invoke(item: Double): Double = item + amount
    }

    data class Append(var string: String) : Operation<String> {
        override fun invoke(item: String): String = item + string
    }

//    data class AppendArray<T>(var item: T): Operation<Array<T>> {
//        override fun invoke(item: Array<T>): Array<T> = item + this.item
//    }
//
//    data class RemoveArray<T>(var item: T): Operation<Array<T>> {
//        override fun invoke(item: Array<T>): Array<T> = item.toMutableList().also{ it.remove(this.item) }.toTypedArray()
//    }

    data class Field<T : Any, V>(val field: MirrorClass.Field<T, V>, val operation: Operation<V>) : Operation<T> {
        override fun invoke(item: T): T {
            val map = item.toAttributeHashMap(field.owner)
            @Suppress("UNCHECKED_CAST")
            map[field.name] = operation.invoke(map[field.name] as V)
            return field.owner.construct(map)
        }
    }

    data class Multiple<T>(val operations: List<Operation<T>>) : Operation<T> {
        @Suppress("UNCHECKED_CAST")
        override fun invoke(item: T): T {
            return if (operations.isNotEmpty() && operations.all { it is Condition.Field<*, *> }) {
                //Optimization to avoid allocations
                val classInfo = operations.first().let { it as Operation.Field<*, *> }.field.owner as ClassInfo<Any>
                val map = (item as Any).toAttributeHashMap(classInfo)
                for (op in operations) {
                    val casted = op as Operation.Field<*, *>
                    map[casted.field.name] = (op as Operation.Field<*, *>).operation
                            .let { it as Operation<Any?> }
                            .invoke(map[casted.field.name])
                }
                return classInfo.construct(map) as T
            } else {
                //Backup
                operations.fold(item) { acc, op -> op.invoke(acc) }
            }
        }
    }


//    
//    data class Place<T : Any, V : Collection<I>, I>(override var field: MirrorClass.Field<T, V>, var element: V) : ModificationOnItem<T, V>() {
//        override fun invoke(item: T) {
//        }
//    }
//
//    
//    data class Remove<T : Any, V : Collection<I>, I>(override var field: MirrorClass.Field<T, V>, var element: V) : ModificationOnItem<T, V>() {
//        override fun invoke(item: T) {
//        }
//    }
}