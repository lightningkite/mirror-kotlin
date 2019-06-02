package com.lightningkite.mirror.archive.model

import com.lightningkite.kommon.collection.treeWalkDepthSequence
import com.lightningkite.mirror.info.MirrorClass


data class Field<NullableT, NotNullT, V>(val field: MirrorClass.Field<NotNullT, V>, val condition: Condition<V>): Condition<NullableT>() where NotNullT: NullableT, NullableT: Any? {
    override fun invoke(item: NullableT): Boolean = item != null && condition.invoke(field.get(item as NotNullT))
    override fun iterable(): Iterable<Condition<*>> = listOf(condition)
}