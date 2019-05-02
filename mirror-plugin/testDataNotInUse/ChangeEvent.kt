package test

data class ChangeEvent<T : Any, V>(override var field: FieldInfo<T, V>, var value: V) : ModificationOnItem<T, V>() {
    override fun invoke(item: MutableMap<String, Any?>)  {
        item[field.name] = value
    }
    override fun invokeOnSub(value: V): V = value
} //test