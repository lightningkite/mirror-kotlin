package com.lightningkite.mirror.gradle

import org.gradle.api.Project
import java.io.File

open class ReflectPluginExtension() {
    var output: File? = null
    var qualifiedList: String? = null

    constructor(project: Project) : this() {
        output = File(project.buildDir, "gen/reflect")
        qualifiedList = "com.lightningkite.mirror.setupGeneratedFor${project.name.filter { it.isLetterOrDigit() }}"
    }
}