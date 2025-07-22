package com.iodigital.lokex

import com.iodigital.lokex.utils.critical
import com.iodigital.lokex.utils.status
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

private const val tokenEnvVar = "LOKALISE_TOKEN"

fun main(args: Array<String>): Unit = runBlocking {
    try {
        val parser = ArgParser(programName = "lokex")

        val token = System.getenv(tokenEnvVar)
            ?: File(".lokalisetoken").takeIf { it.exists() }?.readText()
            ?: let {
                println("Missing Lokalise token as $tokenEnvVar environment variable")
                exitProcess(127)
            }
        val configPath by parser.option(
            ArgType.String,
            shortName = "c",
            fullName = "config",
            description = "LokEx config path"
        ).required()
        val debug by parser.option(
            ArgType.Boolean,
            shortName = "d",
            fullName = "debug-logs",
            description = "Enable debug logs",
        )
        val verbose by parser.option(
            ArgType.Boolean,
            shortName = "v",
            fullName = "verbose-logs",
            description = "Enable debug and verbose logs",
        )
        val status by parser.option(
            ArgType.Boolean,
            shortName = "s",
            fullName = "no-status",
            description = "Hide live status",
        )
        parser.parse(args)


        LokEx.export(
            configFile = File(configPath),
            lokaliseToken = token,
            debugLogs = debug == true,
            verboseLogs = verbose == true,
            showStatus = status != true,
        )

        exitProcess(0)
    } catch (e: Exception) {
        // Delay so other logs are written out and we get a clear stack trace
        delay(1.seconds)
        status("", finalStatus = true)
        critical(tag = "Main", throwable = e, message = "Uncaught exception")
        exitProcess(128)
    }
}