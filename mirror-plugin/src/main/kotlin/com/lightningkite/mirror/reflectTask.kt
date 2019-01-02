package com.lightningkite.mirror

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
                registryName = settings["registryName", "registry", "name"]
                        ?: "com.lightningkite.mirror.GeneratedRegistry",
                outputDirectory = settings["outputDirectory", "output"]?.let { File(mirrorTxtFile.parentFile, it) }
                        ?: mirrorTxtFile.parentFile,
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

data class SourceFileRead(
        val hash: Int,
        val infos: List<ReadClassInfo>,
        val version: Int = VERSION
) {
    companion object {
        const val VERSION = 0
    }
}

fun allDeclarations(directories: List<File>, jarsToInspect: List<File>): Map<String, ReadClassInfo> {
    val mapper = ObjectMapper().registerModule(KotlinModule())
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    val mirrorCacheFile = File("build/mirror-cache.json")
    val cacheType =object : TypeReference<Map<String, SourceFileRead>>() {}
    val previousSourceDeclarations: Map<String, SourceFileRead> = mirrorCacheFile
            .takeIf { it.exists() }
            ?.readText()
            ?.let { mapper.readValue<Map<String, SourceFileRead>>(it, cacheType) }
            ?: mapOf()
    val newSourceDeclarations = HashMap<String, SourceFileRead>()
    val sourceDeclarations = directories.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.extension == "kt" }
            .flatMap { file ->
                val hashCode = file.readText().hashCode()
                val previous = previousSourceDeclarations[file.absolutePath]
                val decls = if(hashCode == previous?.hash && previous.version == SourceFileRead.VERSION) {
                    println("Using cached content for $file...")
                    previous.infos.asSequence()
                } else {
                    println("Reading $file...")
                    file.classes().asSequence()
                            .map {
                                it.fromFile = file
                                it
                            }
                }
                newSourceDeclarations[file.absolutePath] = SourceFileRead(
                        hash = hashCode,
                        infos = decls.toList()
                )
                decls
            }
            .toList()
    //Re-record source declarations
    mirrorCacheFile.bufferedWriter().use {
        mapper.writeValue(it, newSourceDeclarations)
    }
    println("Cache written to $mirrorCacheFile")


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
