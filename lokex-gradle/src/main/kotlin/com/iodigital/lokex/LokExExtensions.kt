package com.iodigital.lokex

import org.gradle.api.Project
import java.io.File

open class LokExExtension(@Suppress("UNUSED_PARAMETER") project: Project) {
    var configFile: File? = null
    var lokaliseToken: String? = null
    var debugLogs: Boolean = false
    var verboseLogs: Boolean = false
}