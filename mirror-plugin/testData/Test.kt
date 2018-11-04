package com.lightningkite.kotlinx.reflection.plugin.test

import com.lightningkite.kotlinx.reflection.ExternalReflection
import java.util.*

@ExternalReflection
class TestClass<T : Any>(
        var name: String,
        var description: String = "",
        var nestMaybe: TestClass<*>? = null,
        normalParam: Int = 2
) : Something<T>(), Another {

    var anotherVar: T? = null

    var child: TestClass<T>? = null

    var childInt: TestClass<Int?>? = null

    val created: Date = Date()

    val somethingElse get() = 3

    fun function(param: Int = 0): Int {
        TestClassReflection.name.set(this, "mah new name")
        return param
    }

    override fun close() {}

    @ExternalReflection
    class TestSubclass(
            var reee: Int = 0
    )
}

@ExternalReflection
interface TestInterface {
    fun test()
}

@ExternalReflection
abstract class TestAbstractClass {

}

@ExternalReflection
open class TestOpenClass {

}

@ExternalReflection
enum class CardinalDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST
}