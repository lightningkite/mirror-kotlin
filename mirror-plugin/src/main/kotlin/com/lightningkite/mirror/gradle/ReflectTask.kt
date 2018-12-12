package com.lightningkite.mirror.gradle

//import com.lightningkite.mirror.old.reflectTask
import com.lightningkite.mirror.reflectTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class ReflectTask() : DefaultTask() {

    init{
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {
        reflectTask(
                directories = listOf(project.file("src")) + project.configurations.filter { it.isCanBeResolved }.flatMap { it }
        )
    }

}
