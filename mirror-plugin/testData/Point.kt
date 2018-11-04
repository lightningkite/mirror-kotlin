package com.lightningkite.kotlinx.math

import com.lightningkite.kotlinx.reflection.ExternalReflection
import kotlin.math.sqrt

@Suppress("NOTHING_TO_INLINE")
@ExternalReflection
data class Point(var x: Float = 0f, var y: Float = 0f) {

    companion object {
        val Zero = Point(0f, 0f)
        fun polar(angle: Angle, length: Float) = Point(angle.cos() * length, angle.sin() * length)
    }

    inline val lengthSquared: Float
        get() = x.squared() + y.squared()
    inline var length: Float
        get() = sqrt(x.squared() + y.squared().toDouble()).toFloat()
        set(value) {
            this *= value / length
        }

    inline var angle: Angle
        get() = Angle.atan2(y, x)
        set(value) {
            val len = length
            x = value.cos() * len
            y = value.sin() * len
        }

    inline operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    inline operator fun plusAssign(other: Point) {
        x += other.x
        y += other.y
    }

    inline operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
    inline operator fun minusAssign(other: Point) {
        x -= other.x
        y -= other.y
    }

    inline operator fun times(other: Point): Point = Point(x * other.x, y * other.y)
    inline operator fun timesAssign(other: Point) {
        x *= other.x
        y *= other.y
    }

    inline operator fun div(other: Point): Point = Point(x / other.x, y / other.y)
    inline operator fun divAssign(other: Point) {
        x /= other.x
        y /= other.y
    }

    inline operator fun times(scalar: Float): Point = Point(x * scalar, y * scalar)
    inline operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
    }

    inline operator fun div(scalar: Float): Point = Point(x / scalar, y / scalar)
    inline operator fun divAssign(scalar: Float) {
        x /= scalar
        y /= scalar
    }

    inline infix fun dot(other: Point): Float = x * other.x + y * other.y
    inline infix fun cross(other: Point): Float = x * other.y - y * other.x
    inline infix fun project(other: Point): Point {
        val len = this dot other
        return Point(other.x * len, other.y * len)
    }

    inline infix fun projectAssign(other: Point) {
        val len = this dot other
        x = other.x * len
        y = other.y * len
    }

    inline fun perpendicular() = Point(-y, x)
    inline fun perpendicularAssign() {
        val temp = x
        x = -y
        y = temp
    }

    inline infix fun distance(other: Point): Float = sqrt(distanceSquared(other))
    inline infix fun distanceSquared(other: Point): Float = (other.x - x).squared() + (other.y - y).squared()

    inline infix fun angleTo(other: Point): Angle = Angle.atan2(other.y - y, other.x - x)
}
