package com.lightningkite.mirror.info

@Suppress("UNCHECKED_CAST")
inline val <Owner, Type> ((Owner) -> Type).untyped
    get() = this as ((Any) -> Any?)

@Suppress("UNCHECKED_CAST")
inline val <Owner, Type> ((Owner, Type) -> Unit).untyped
    get() = this as ((Any, Any?) -> Unit)