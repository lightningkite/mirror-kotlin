package com.lightningkite.mirror

import com.lightningkite.mirror.metadata.readPackageFragment
import com.lightningkite.mirror.source.classes
import java.io.File
import java.util.jar.JarFile


fun reflectTask(directories: List<File>) {
    val sourceDirectories = directories.filter { it.isDirectory }
    val jarsToInspect = directories.filter { it.extension == "jar" }
    println("Checking source directories: $sourceDirectories")
    println("Checking jars: $jarsToInspect")

    val requestFiles = sourceDirectories.asSequence().flatMap {
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

    val declarations = allDeclarations(sourceDirectories, jarsToInspect)

    for (file in requestFiles) {
        file.output(declarations)
    }
}

fun allDeclarations(directories: List<File>, jarsToInspect: List<File>): Map<String, ReadClassInfo> {

    val sourceDeclarations = directories.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.extension == "kt" }
            .flatMap { file ->
                println("Reading $file...")
                file.classes().asSequence()
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
                            println("Reading: ${subfile.name} from $it")
                            file.getInputStream(subfile).readPackageFragment().read().asSequence()
                        }
            }

    return (sourceDeclarations + libraryDeclarations)
            .associateBy { it.qualifiedName }
}
