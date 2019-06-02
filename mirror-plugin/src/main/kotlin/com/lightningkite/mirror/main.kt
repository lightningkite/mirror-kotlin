package com.lightningkite.mirror

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    reflectTask(
            directories = listOf(
                    File("C:\\Users\\josep\\Projects\\krosslin\\mirror-kotlin\\mirror-plugin\\testData"),
                    File("C:\\Users\\josep\\Projects\\krosslin\\kommon\\build\\libs\\kommon-metadata-0.1.8.jar")
            )
    )
    println("SUCCESS!")
}
