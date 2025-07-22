package com.iodigital.lokex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class ExportLokaliseTask : DefaultTask() {


    override fun getDescription() = "Exports the Lokalise files"

    private val extension = project.extensions.findByType(LokExExtension::class.java)

    @TaskAction
    fun export() {
        with(extension ?: LokExExtension(project)) {
            LokEx.exportBlocking(
                configFile = requireNotNull(configFile) { "LokEx config file not configured, add `lokex { configFile = File(...) }`" },
                lokaliseToken = requireNotNull(lokaliseToken) { "Lokalise token not configured, add `lokex { lokaliseToken = File(...).readText().trim() }`" },
                debugLogs = debugLogs,
                verboseLogs = verboseLogs,
            )
        }
    }
}