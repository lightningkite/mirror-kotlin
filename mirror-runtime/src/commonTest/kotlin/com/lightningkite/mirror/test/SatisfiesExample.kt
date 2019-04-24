package com.lightningkite.mirror.test

sealed class SatisfiesExample<T> {
    class First<T> : SatisfiesExample<T>()
    class Second<T> : SatisfiesExample<T>()
    class Third<T> : SatisfiesExample<T>()
}