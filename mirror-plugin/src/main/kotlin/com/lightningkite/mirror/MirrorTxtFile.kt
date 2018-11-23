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
                "kotlin.Any",
                "kotlin.Unit",
                "kotlin.Boolean",
                "kotlin.Byte",
                "kotlin.Short",
                "kotlin.Int",
                "kotlin.Long",
                "kotlin.Float",
                "kotlin.Double",
                "kotlin.Number",
                "kotlin.Char",
                "kotlin.String",
                "kotlin.collections.List",
                "kotlin.collections.Map",
                "Any",
                "Unit",
                "Boolean",
                "Byte",
                "Short",
                "Int",
                "Long",
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
                    .map { it.kClass }
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
        val needed = neededReflections(declarations)
        val reflectionsToWrite = reflectionsToWrite(declarations, needed)

        //Output the other files
        for (decl in declarations.values) {
            if(decl.qualifiedName !in reflectionsToWrite) continue
            val written = decl.toString()
            File(outputDirectory, decl.reflectionName + ".kt").let{
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
        File(outputDirectory, registryName.substringAfterLast('.') + ".kt").writeText("""
        |package ${registryName.substringBeforeLast('.')}
        |
        |import com.lightningkite.kommon.native.SharedImmutable
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |@SharedImmutable
        |val ${registryName.substringAfterLast('.')} = ClassInfoRegistry(
        |${declarations.asSequence().filter { it.key in needed }.map { it.value.packageName + "." + it.value.reflectionName }.joinToString(",\n    ", "    ")}
        |)
    """.trimMargin())
    }
}