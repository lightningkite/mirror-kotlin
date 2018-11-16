package com.lightningkite.kotlinx.persistence

data class ChangeEvent<T : Any, V>(override var field: SerializedFieldInfo<T, V>, var value: V) : ModificationOnItem<T, V>() {
    override fun invoke(item: MutableMap<String, Any?>)  {
        item[field.name] = value
    }
    override fun invokeOnSub(value: V): V = value
}