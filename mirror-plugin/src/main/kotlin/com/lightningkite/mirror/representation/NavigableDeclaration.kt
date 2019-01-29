package com.lightningkite.mirror.representation

import com.lightningkite.mirror.recursiveFlatMap

interface NavigableDeclaration {
    fun subs(): Sequence<NavigableDeclaration>
}

fun NavigableDeclaration.recursiveSubs() = subs().recursiveFlatMap { it.subs() }