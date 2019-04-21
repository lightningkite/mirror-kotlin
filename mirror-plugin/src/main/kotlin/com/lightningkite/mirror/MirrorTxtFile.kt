package com.lightningkite.mirror

import com.lightningkite.mirror.output.writeAnnotation
import com.lightningkite.mirror.output.writeMirror
import com.lightningkite.mirror.representation.ReadClassInfo
import java.io.File

class MirrorTxtFile(
        val registryName: String,
        val outputDirectory: File,
        val qualifiedNames: List<String>
) {
    fun neededReflections(declarations: Map<String, ReadClassInfo>): List<ReadClassInfo> {
        val reflectionHandledNames = declarations.values.asSequence()
                .filter { it.name.endsWith("Mirror") && it.fromFile?.parentFile != outputDirectory }
                .map { it.qualifiedName }
                .toSet()

        val allNames = ArrayList<ReadClassInfo>()
        val toCheck = ArrayList<String>()
        val alreadyAdded = hashSetOf(
                "kotlin.reflect.KClass",
                "kotlin.Any",
                "kotlin.Unit",
                "kotlin.Boolean",
                "kotlin.Byte",
                "kotlin.Short",
                "kotlin.Int",
                "kotlin.Long",
                "kotlin.UByte",
                "kotlin.UShort",
                "kotlin.UInt",
                "kotlin.ULong",
                "kotlin.Float",
                "kotlin.Double",
                "kotlin.Number",
                "kotlin.Char",
                "kotlin.String",
                "kotlin.collections.List",
                "kotlin.collections.Map",
                "KClass",
                "Any",
                "Unit",
                "Boolean",
                "Byte",
                "Short",
                "Int",
                "Long",
                "UByte",
                "UShort",
                "UInt",
                "ULong",
                "Float",
                "Double",
                "Number",
                "Char",
                "String",
                "List",
                "Map"
        )
        toCheck.addAll(qualifiedNames)
        alreadyAdded.addAll(qualifiedNames)
        qualifiedNames.asSequence()
                .filter { it.endsWith('*') }
                .map { it.removeSuffix("*") }
                .forEach { pattern ->
                    declarations.keys.asSequence()
                            .filter { !it.endsWith("Mirror") }
                            .filter { it.startsWith(pattern) }
                            .forEach {
                                toCheck.add(it)
                            }
                }

        while (toCheck.isNotEmpty()) {
            val next = toCheck.removeAt(toCheck.lastIndex)
            val declaration = declarations[next] ?: continue
            if(declaration.reflectionQualifiedName in reflectionHandledNames) continue
            allNames.add(declaration)
            val otherNames = declaration.fields.asSequence()
                    .map { it.type }
                    .plus(declaration.implements)
                    .recursiveFlatMap {
                        it.typeArguments.asSequence().map { it.type }
                    }
                    .map { it.kclass }
                    .filter { it.isNotBlank() }
                    .flatMap { kClass ->
                        if (kClass[0].isUpperCase()) {
                            //It's a non-qualified name.
                            //We need to find it.
                            if(kClass.contains('.')){
                                val pre = kClass.substringBefore('.')
                                val post = kClass.substringAfter('.')
                                val immediate = declaration.imports.asSequence().find { it.endsWith(pre) }
                                if (immediate != null) {
                                    sequenceOf("$immediate.$post")
                                } else {
                                    declaration.imports.asSequence()
                                            .filter { it.endsWith('*') }
                                            .map { it.removeSuffix("*").plus(kClass) }
                                            .plus(declaration.packageName.plus(".").plus(kClass))
                                }
                            } else {
                                val immediate = declaration.imports.asSequence().find { it.endsWith(kClass) }
                                if (immediate != null) {
                                    sequenceOf(immediate)
                                } else {
                                    declaration.imports.asSequence()
                                            .filter { it.endsWith('*') }
                                            .map { it.removeSuffix("*").plus(kClass) }
                                            .plus(declaration.packageName.plus(".").plus(kClass))
                                }
                            }
                        } else {
                            //It's probably a qualified name.
                            //We'll just take it.
                            sequenceOf(kClass)
                        }
                    }
                    .filter { it !in alreadyAdded }
                    .toList()
            alreadyAdded.addAll(otherNames)
            toCheck.addAll(otherNames)
        }
        return allNames
    }

    fun neededAnnotations(declarations: Map<String, ReadClassInfo>, declarationsToWrite: List<ReadClassInfo>): List<ReadClassInfo> {
        val handledNames = declarations.values.asSequence()
                .filter { it.name.endsWith("Mirror") && it.fromFile?.parentFile != outputDirectory }
                .map { it.qualifiedName }
        return declarationsToWrite.asSequence()
                .flatMap { declaration ->
                    declaration.annotations.asSequence().flatMap { anno ->
                        val kClass = anno.name
                        if (kClass[0].isUpperCase()) {
                            //It's a non-qualified name.
                            //We need to find it.
                            val immediate = declaration.imports.asSequence().find { it.endsWith(kClass) }
                            if (immediate != null) {
                                sequenceOf(immediate)
                            } else {
                                declaration.imports.asSequence()
                                        .filter { it.endsWith('*') }
                                        .map { it.removeSuffix("*").plus(kClass) }
                                        .plus(declaration.packageName.plus(".").plus(kClass))
                            }
                        } else {
                            //It's probably a qualified name.
                            //We'll just take it.
                            sequenceOf(kClass)
                        }
                    }
                }
                .distinct()
                .mapNotNull { declarations[it] }
                .filter { it.reflectionQualifiedName !in handledNames }
                .toList()
    }

    fun output(
            declarations: Map<String, ReadClassInfo>
    ) {
        outputDirectory.mkdirs()
        val reflectionsToWrite = neededReflections(declarations)
        val allReflectionsToWrite = neededAnnotations(declarations, reflectionsToWrite) + reflectionsToWrite

        val filesWritten = ArrayList<File>()

        //Output the mirrors
        for (decl in allReflectionsToWrite) {
            val written = buildString { TabWriter(this).writeMirror(decl) }
            File(outputDirectory, decl.accessName + ".mirror.kt").let {
                filesWritten.add(it)
                if (it.exists()) {
                    if (it.readText() == written) {
                        println("Checked up-to-date ${decl.reflectionName}...")
                        return@let
                    }
                }
                println("Updating ${decl.reflectionName}...")
                it.writeText(written)
            }
        }

        println("Updating ${registryName.substringAfterLast('.')}...")
        //Write the final file
        val registryFile = File(outputDirectory, registryName.substringAfterLast('.') + ".kt")
        filesWritten.add(registryFile)
        registryFile.writeText("""
        |package ${registryName.substringBeforeLast('.')}
        |
        |//${ReadClassInfo.GENERATED_NOTICE}
        |
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |fun ${registryName.substringAfterLast('.')}() = MirrorRegistry.register(
        |${allReflectionsToWrite.filter { ReadClassInfo.Modifier.Annotation !in it.modifiers }.joinToString(",\n    ", "    ") { it.reflectionQualifiedNameMin }}
        |)
    """.trimMargin())

        outputDirectory.listFiles().filter { it !in filesWritten && (it.readText().contains(ReadClassInfo.GENERATED_NOTICE)) }.forEach { it.delete() }
    }
}