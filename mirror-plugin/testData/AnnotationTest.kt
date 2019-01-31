package test

annotation class CustomAnnotation(val x: Int = 0)

@CustomAnnotation
data class AnnotatedClass(val y: String = "")