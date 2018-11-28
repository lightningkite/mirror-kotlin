package com.lightningkite.mirror

import com.lightningkite.mirror.metadata.readPackageFragment
import com.lightningkite.mirror.source.getFileClasses
import com.lightningkite.mirror.source.kotlinNode
import java.io.File
import java.util.jar.JarFile


fun reflectTask(directories: List<File>, jarsToInspect: List<File>) {
    println("Checking source directories: $directories")
    println("Checking jars: $jarsToInspect")

    val requestFiles = directories.asSequence().flatMap {
        it.walkTopDown().filter { it.endsWith("mirror.txt") }
    }.map { mirrorTxtFile ->
        val lines = mirrorTxtFile.readLines()
        val settings = lines.filter { it.contains('=') }
                .associate { it.substringBefore('=').trim() to it.substringAfterLast('=').trim() }
        val otherLines = lines.filter { !it.contains('=') && it.isNotBlank() }
        MirrorTxtFile(
                registryName = settings["registryName", "registry", "name"] ?: "com.lightningkite.mirror.GeneratedRegistry",
                outputDirectory = settings["outputDirectory", "output"]?.let { File(mirrorTxtFile.parentFile, it) } ?: mirrorTxtFile.parentFile,
                qualifiedNames = otherLines
        )
    }.toList()
    if (requestFiles.isEmpty()) return
    val requestedNames = requestFiles.flatMap { it.qualifiedNames }

    println("Requested names: $requestedNames")

    val declarations = allDeclarations(directories, jarsToInspect)

    for (file in requestFiles) {
        file.output(declarations)
    }
}

fun allDeclarations(directories: List<File>, jarsToInspect: List<File>): Map<String, ReadClassInfo> {

    val sourceDeclarations = directories.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.extension == "kt" }
            .flatMap { file ->
                file.kotlinNode().getFileClasses().asSequence()
                        .map {
                            it.fromFile = file
                            it
                        }
            }

    val libraryDeclarations = jarsToInspect.asSequence()
            .flatMap {
                val file = JarFile(it)
                file.entries().asSequence()
                        .filter { it.name.endsWith("kotlin_metadata") }
                        .flatMap { subfile ->
                            file.getInputStream(subfile).readPackageFragment().read().asSequence()
                        }
            }

    return (sourceDeclarations + libraryDeclarations)
            .associateBy { it.qualifiedName }
}