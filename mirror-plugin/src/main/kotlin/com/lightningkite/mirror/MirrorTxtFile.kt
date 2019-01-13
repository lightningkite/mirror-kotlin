package com.lightningkite.mirror

import java.io.File

class MirrorTxtFile(
        val registryName: String,
        val outputDirectory: File,
        val qualifiedNames: List<String>
) {
    fun neededReflections(declarations: Map<String, ReadClassInfo>):List<String>{
        val allNames = ArrayList<String>()
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
                "kotlin.text.Regex",
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
                "Map",
                "Regex"
        )
        toCheck.addAll(qualifiedNames)
        alreadyAdded.addAll(qualifiedNames)
        while(toCheck.isNotEmpty()){
            val next = toCheck.removeAt(toCheck.lastIndex)
            allNames.add(next)
            val declaration = declarations[next] ?: continue
            val otherNames = declaration.fields.asSequence()
                    .map { it.type }
                    .plus(declaration.implements)
                    .recursiveFlatMap {
                        it.typeArguments.asSequence().map { it.type }
                    }
                    .map { it.kclass }
                    .filter { it.isNotBlank() }
                    .flatMap { kClass ->
                        if(kClass[0].isUpperCase()){
                            //It's a non-qualified name.
                            //We need to find it.
                            val immediate = declaration.imports.asSequence().find { it.endsWith(kClass) }
                            if(immediate != null){
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
                    .filter { it !in alreadyAdded }
                    .toList()
            alreadyAdded.addAll(otherNames)
            toCheck.addAll(otherNames)
        }
        return allNames
    }

    fun reflectionsToWrite(declarations: Map<String, ReadClassInfo>, needed:List<String>):List<String>{
        val classInfoNames = declarations.values.asSequence()
                .filter { it.name.endsWith("ClassInfo") && it.fromFile?.parentFile != outputDirectory }
                .map { it.name }
        return needed.filter {
            declarations[it]?.reflectionName !in classInfoNames
        }
    }

    fun output(
            declarations: Map<String, ReadClassInfo>
    ){
        outputDirectory.mkdirs()
        val needed = neededReflections(declarations)
        val reflectionsToWrite = reflectionsToWrite(declarations, needed)

        val filesWritten = ArrayList<File>()

        //Output the other files
        for (decl in declarations.values) {
            if(decl.qualifiedName !in reflectionsToWrite) continue
            val written = decl.toString()
            File(outputDirectory, decl.reflectionName + ".kt").let{
                filesWritten.add(it)
                if(it.exists()){
                    if(it.readText() == written) {
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
        |import com.lightningkite.kommon.native.SharedImmutable
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |@SharedImmutable
        |val ${registryName.substringAfterLast('.')} = ClassInfoRegistry(
        |${declarations.asSequence().filter { it.key in needed }.map { it.value.reflectionQualifiedName }.joinToString(",\n    ", "    ")}
        |)
    """.trimMargin())

        outputDirectory.listFiles().filter { it !in filesWritten && !it.name.startsWith("mirror") }.forEach { it.delete() }
    }
}