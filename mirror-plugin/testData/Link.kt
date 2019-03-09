package com.lightningkite.mirror.archive.model

interface Link<A : HasId, B : HasId> {
    val a: Reference<A>
    val b: Reference<B>
}