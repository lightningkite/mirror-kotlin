package test

data class DefaultsTest(
        val x: Int = 2,
        val y: Float = .23f,
        val noDefault: String,
        val z: String = "default"
)