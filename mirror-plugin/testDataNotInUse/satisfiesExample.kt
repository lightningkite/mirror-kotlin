package com.lightningkite.mirror.test

sealed class SatisfiesExample<in T> {
    object AnyTest: SatisfiesExample<Any?>()
    class First<T> : SatisfiesExample<T>()
    class Second<T> : SatisfiesExample<T>()
    class Third<T> : SatisfiesExample<T>()
}