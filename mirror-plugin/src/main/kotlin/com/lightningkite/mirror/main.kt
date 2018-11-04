package com.lightningkite.mirror

import java.io.File


fun main(vararg args: String) {
    println("HERE WE GO!!!")
    reflectTask(
            directories = listOf(File("C:\\Users\\josep\\Projects\\krosslin\\mirror-plugin\\testData")),
            jarsToInspect = listOf(File("C:\\Users\\josep\\.m2\\repository\\com\\lightningkite\\recktangle-metadata\\0.0.1\\recktangle-metadata-0.0.1.jar"))
    )
    println("SUCCESS!")
}