package com.lightningkite.mirror.old

import com.lightningkite.mirror.source.kotlinNode
import java.io.File


fun main(vararg args: String) {
    val basePath = "C:\\Users\\josep\\Projects\\kotlinx-all\\kotlinx-reflect-plugin"
    val lookForSources = listOf(File("$basePath/testData"))
    val output = File("$basePath/testOutput")
    val qualifiedSetupFunctionName = "com.lightningkite.mirror.setupGenerated"

    lookForSources.forEach {
        it.walkTopDown().forEach {
            println(it)
            if (it.extension == "kt") {
                it.kotlinNode().print(System.out)
            }
        }
    }

//    reflectTask(lookForSources.asSequence().flatMap { it.walkTopDown() }.toList(), output, qualifiedSetupFunctionName)
}
