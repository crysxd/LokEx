package com.iodigital.lokex

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class LokExPluginText {
    @Test
    fun pluginRegistersATask() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.iodigital.lokex")

        // Verify the result
        println(project.tasks.joinToString { it.name })
        Assert.assertNotNull(project.tasks.findByName("exportLokalise"))
    }
}
