package com.lightningkite.mirror.gradle

//import com.lightningkite.mirror.old.reflectTask
import com.lightningkite.mirror.reflectTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ReflectTask() : DefaultTask() {

    init {
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {
        val files = ArrayList<File>()
        files.add(project.file("src"))
        try {
            for (config in project.configurations) {
                if(!config.name.contains("common", true)) continue
                try {
                    for (file in config) {
                        try {
                            files.add(file)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Throwable) {
                }
            }
        } catch (e: Throwable) {
        }
        println("Files: ${files.joinToString("\n")}")
        val cacheFile = File("build/mirror-cache-${project.name}.json")
        println("Cache: $cacheFile")
        reflectTask(
                directories = files,
                mirrorCacheFile = cacheFile
        )
    }

}
