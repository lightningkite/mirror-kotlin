package test

data class DefaultsTest(
        val x: Int = 2,
        val y: Float = .23f,
        var noDefault: String,
        var z: String = "default"
)