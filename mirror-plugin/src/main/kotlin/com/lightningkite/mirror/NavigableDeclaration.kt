package com.lightningkite.mirror

interface NavigableDeclaration {
    fun subs(): Sequence<NavigableDeclaration>
}

fun NavigableDeclaration.recursiveSubs() = subs().recursiveFlatMap { it.subs() }