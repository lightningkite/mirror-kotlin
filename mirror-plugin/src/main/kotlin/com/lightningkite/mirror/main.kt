package com.lightningkite.mirror

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    reflectTask(
            directories = listOf(
                    File("C:\\Users\\josep\\Projects\\krosslin\\mirror-kotlin\\mirror-plugin\\testData")
            )
    )
    println("SUCCESS!")
}
