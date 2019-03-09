package com.lightningkite.mirror.archive.model

import kotlin.random.Random

data class Uuid(
        val mostSignificantBits: Long,
        val leastSignificantBits: Long
) : Comparable<Uuid> {

    override fun compareTo(other: Uuid): Int {
        return if (this.mostSignificantBits == other.mostSignificantBits)
            this.leastSignificantBits.compareTo(other.leastSignificantBits)
        else this.mostSignificantBits.compareTo(other.mostSignificantBits)
    }

    companion object {
        fun fromUUIDString(input: String): Uuid {
            val hexOnly = input.filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
            if (hexOnly.length != 32) throw IllegalArgumentException("String is not a Uuid, should be 32 hex characters")
            return Uuid(
                    mostSignificantBits = hexOnly.substring(0, 16).toULong(16).toLong(),
                    leastSignificantBits = hexOnly.substring(16, 32).toULong(16).toLong()
            )
        }

        fun randomUUID4(): Uuid = Uuid(
                mostSignificantBits = (Random.nextLong() and (-1L - 0x000000000000F000) or (0x4000)),
                leastSignificantBits = (Random.nextLong() and 0x3FFFFFFFFFFFFFFFL or (0x8000_0000L shl 32))
        )
    }

    fun toUUIDString(): String {
        val first = mostSignificantBits.toULong().toString(16).padStart(16, '0')
        val second = leastSignificantBits.toULong().toString(16).padStart(16, '0')
        return buildString(36) {
            append(first.substring(0, 8))
            append("-")
            append(first.substring(8, 12))
            append("-")
            append(first.substring(12, 16))
            append("-")
            append(second.substring(0, 4))
            append("-")
            append(second.substring(4, 16))
        }
    }

    override fun toString(): String = toUUIDString()
}