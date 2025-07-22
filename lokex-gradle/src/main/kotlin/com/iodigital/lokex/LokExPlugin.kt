package com.iodigital.lokex


import org.gradle.api.Plugin
import org.gradle.api.Project

class LokExPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register(
            "exportLokalise",
            ExportLokaliseTask::class.java
        )

        project.extensions.create(
            "lokex",
            LokExExtension::class.java,
            project
        )
    }
}
