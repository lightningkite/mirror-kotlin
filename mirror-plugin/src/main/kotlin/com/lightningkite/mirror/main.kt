package com.lightningkite.mirror

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    reflectTask(
            directories = listOf(File("C:\\Users\\josep\\Projects\\krosslin\\mirror-kotlin\\mirror-plugin\\testData")),
            jarsToInspect = listOf(File("C:\\Users\\josep\\.m2\\repository\\com\\lightningkite\\mirror-archive-api-metadata\\0.0.2\\mirror-archive-api-metadata-0.0.2.jar"))
    )
    println("SUCCESS!")
}