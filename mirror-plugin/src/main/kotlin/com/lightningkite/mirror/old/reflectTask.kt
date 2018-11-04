//package com.lightningkite.mirror.old
//
//import com.lightningkite.mirror.source.getFile
//import com.lightningkite.mirror.source.kotlinNode
//import java.io.File
//
//data class ProcessedMetadata(val filename: String, val hash: String, val classNames: List<String>) {
//    companion object {
//        fun parse(string: String): ProcessedMetadata {
//            val parts = string.split('|')
//            return ProcessedMetadata(parts[0], parts[1], parts.subList(2, parts.size))
//        }
//    }
//
//    override fun toString() = filename + "|" + hash + "|" + classNames.joinToString("|")
//}
//
//fun reflectTask(inputFiles: List<File>, output: File, setupFunctionQualifiedName: String) {
//
//    val myVersion = "0.0.9"
//    val previouslyProcessedFile = File(output, "previous.txt")
//    val previouslyProcessed = previouslyProcessedFile
//            .takeIf { it.exists() }
//            ?.readLines()
//            ?.let{
//                if(it.size > 1 && it.first() == myVersion){
//                    it.subList(1, it.size)
//                } else null
//            }
//            ?.mapNotNull { try{
//                ProcessedMetadata.parse(it)
//            } catch(e:Exception){ null } }
//            ?.toSet()
//            ?.associate { it.filename + it.hash to it } ?: mapOf()
//    val newlyProcessed = ArrayList<ProcessedMetadata>()
//
//    run {
//        //Search for files based on the ExternalReflection annotation
//        for (it in inputFiles) {
//            try {
//                if (it.extension != "kt") continue
//                val fileText = it.readText()
//                if (!fileText.contains("ExternalReflection")) continue
//
//                val hashCode = it.toString() + fileText.hashCode().toString()
//
//                val previousData = previouslyProcessed[hashCode]
//                if(previousData != null) {
//                    println("Skipping $previousData, up to date...")
//                    newlyProcessed.add(previousData)
//                    continue
//                }
//                println("Processing $it...")
//
//                val file = it.kotlinNode().getFile(it.name).let{
//                    it.copy(classes = it.classes.filter { it.annotations.any { it.name == "ExternalReflection" } })
//                }
//                try {
//                    val packageFolder = if(file.packageName.isNotBlank()) file.packageName.replace('.', File.separatorChar) + "/" else ""
//                    val out = output.resolve(packageFolder + file.fileName)
//                    out.parentFile.mkdirs()
//                    out.bufferedWriter().use {
//                        val tabWriter = TabWriter(it)
//                        tabWriter.write(file)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                val processed = ProcessedMetadata(it.toString(), fileText.hashCode().toString(), file.classes.map { it.qualifiedReflectiveObjectName })
//                newlyProcessed += processed
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    //Write what was processed
//    if(!previouslyProcessedFile.exists()) {
//        previouslyProcessedFile.parentFile.mkdirs()
//        previouslyProcessedFile.createNewFile()
//    }
//    previouslyProcessedFile.writeText(myVersion + "\n" + newlyProcessed.joinToString("\n"))
//
//    //Clean up the remaining files that aren't used anymore
//    run {
//        val processed = newlyProcessed.map { it.filename.substringAfterLast(File.separatorChar) }.toSet()
//        output.walkTopDown().forEach {
//            if (it.extension == "kt" && it.name !in processed) {
//                println("Removing $it...")
//                it.delete()
//            }
//        }
//    }
//
//    println("Writing master files...")
//    try {
//        KxvDirectory(setupFunctionQualifiedName, newlyProcessed.flatMap { it.classNames })
//                .let { kxvDirectory ->
//                    val out = output.resolve(kxvDirectory.qualifiedValName.replace('.', File.separatorChar) + ".kt")
//                    out.parentFile.mkdirs()
//                    out.bufferedWriter().use {
//                        val tabWriter = TabWriter(it)
//                        tabWriter.write(kxvDirectory)
//                    }
//                }
////        files
////                .asSequence()
////                .flatMap { it.classes.asSequence() }
////                .groupBy { it.packageName }
////                .map { KxvDirectory(it.key + ".reflections", it.value) }
////                .toList()
////                .forEach { kxvDirectory ->
////                    val out = output.resolve(kxvDirectory.qualifiedValName.replace('.', File.separatorChar) + ".kt")
////                    out.parentFile.mkdirs()
////                    out.bufferedWriter().use {
////                        val tabWriter = TabWriter(it)
////                        tabWriter.write(kxvDirectory)
////                    }
////                }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    println("Complete.")
//}