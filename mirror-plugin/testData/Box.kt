package test

data class Box<T : Number>(val value: T) : TestInterface

interface TestInterface {}