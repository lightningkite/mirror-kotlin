package com.lightningkite.mirror.serialization

interface SerializationGenerator: Comparable<SerializationGenerator> {
    val description: String
    val priority: Float
    override fun compareTo(other: SerializationGenerator): Int = other.priority.compareTo(priority)
}