package com.lightningkite.mirror.gradle

import com.lightningkite.mirror.javaify
import com.lightningkite.mirror.javaifyWithDots
import org.gradle.api.Plugin
import org.gradle.api.Project


open class ReflectPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val projectName = target.name.javaify()
        val groupName = target.group.toString().javaifyWithDots()

        val task = target.tasks.create("mirror", ReflectTask::class.java)
    }
}

