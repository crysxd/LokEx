package com.iodigital.lokex

import com.iodigital.lokex.api.LokaliseApi
import com.iodigital.lokex.models.lokalise.LokaliseKey
import com.iodigital.lokex.models.lokalise.LokaliseTranslation
import com.iodigital.lokex.models.lokex.LokExConfig
import com.iodigital.lokex.models.lokex.LokExConfig.ExportEmptyAs.Base
import com.iodigital.lokex.models.lokex.LokExConfig.ExportEmptyAs.Empty
import com.iodigital.lokex.models.lokex.LokExConfig.ExportEmptyAs.Skip
import com.iodigital.lokex.models.lokex.LokExConfig.Platform.Android
import com.iodigital.lokex.models.lokex.LokExConfig.Platform.Other
import com.iodigital.lokex.models.lokex.LokExConfig.Platform.Web
import com.iodigital.lokex.models.lokex.LokExConfig.Platform.iOS
import com.iodigital.lokex.models.lokex.LokExString
import com.iodigital.lokex.models.lokex.LokExStringCache
import com.iodigital.lokex.serializer.DefaultJson
import com.iodigital.lokex.utils.cacheDir
import com.iodigital.lokex.utils.critical
import com.iodigital.lokex.utils.debug
import com.iodigital.lokex.utils.info
import com.iodigital.lokex.utils.startStatusAnimation
import com.iodigital.lokex.utils.status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

private const val tag = "LokEx"


object LokEx {

    fun exportBlocking(
        configFile: File,
        lokaliseToken: String,
        showStatus: Boolean = true,
        debugLogs: Boolean = false,
        verboseLogs: Boolean = false,
    ) = runBlocking {
        export(
            configFile = configFile,
            lokaliseToken = lokaliseToken,
            showStatus = showStatus,
            debugLogs = debugLogs,
            verboseLogs = verboseLogs
        )
    }

    suspend fun export(
        configFile: File,
        lokaliseToken: String,
        showStatus: Boolean = true,
        debugLogs: Boolean = false,
        verboseLogs: Boolean = false,
    ) {
        com.iodigital.lokex.utils.showStatus = showStatus
        com.iodigital.lokex.utils.debugLogs = debugLogs
        com.iodigital.lokex.utils.verboseLogs = verboseLogs

        val exportScope = CoroutineScope(Dispatchers.IO)
        try {
            startStatusAnimation(exportScope)
            status("Starting...")
            debug(tag = tag, "Using cache at $cacheDir")
            val configJson = configFile.readText()
            val config = DefaultJson.decodeFromString<LokExConfig>(configJson)
            val exports = loadFiles(config = config, token = lokaliseToken, scope = exportScope)
            performExports(configFile, exports)
            status("Export completed successfully.", finalStatus = true)
        } finally {
            exportScope.cancel("Export done")
        }
    }

    private suspend fun loadFiles(
        config: LokExConfig,
        token: String,
        scope: CoroutineScope,
    ) = withContext(Dispatchers.IO) {
        val api = LokaliseApi(token = token, scope = scope)
        val allProjects = config.exports.flatMap { it.projectIds }.toSet()
        val strings = allProjects.associateWith { projectId ->
            info(tag, "Loading Lokalise file: $projectId")
            async { api.exportStrings(projectId) }
        }.mapValues { (_, x) ->
            x.await()
        }

        config.exports.associateWith { export ->
            info(tag, "Exporting Lokalise project:")
            info(tag, "  Project: ${export.projectId}")
            info(tag, "  Language: ${export.language}")

            strings
                .filter { (projectId, _) -> projectId in export.projectIds }
                .flatMap { (_, strings) -> strings.export(export) }
                .reversed()
                .distinctBy { it.key }
        }
    }

    private fun LokExStringCache.export(
        export: LokExConfig.Export
    ) = strings.filter { (key, _) ->
        export.filterTags.isEmpty() || key.tags?.any { tag -> tag in export.filterTags } == true
    }.filter { (key, _) ->
        export.platform.lokaliseName in (key.platforms ?: emptyList())
    }.mapNotNull { (key, translations) ->
        try {
            processKey(
                key = key,
                platform = export.platform,
                translations = translations.associateBy { it.languageIso },
                exportEmptyAs = export.exportEmptyAs,
                languageIso = export.language,
                baseLanguageIso = baseLanguageIso
            )
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to process key ${key.keyName?.other} of project $projectId",
                e
            )
        }
    }

    private fun processKey(
        key: LokaliseKey,
        platform: LokExConfig.Platform,
        translations: Map<String, LokaliseTranslation>,
        exportEmptyAs: LokExConfig.ExportEmptyAs,
        languageIso: String,
        baseLanguageIso: String,
    ): LokExString? {
        val keyName = when (platform) {
            iOS -> key.keyName?.ios
            Android -> key.keyName?.android
            Web -> key.keyName?.web
            Other -> key.keyName?.other
        } ?: key.keyId.toString().also {
            debug(tag, "Failed to get key name for id ${key.keyId}, using id instead")
        }

        return if (key.isPlural == true) {
            val translationVariants = parsePlurals(
                key = keyName,
                value = translations[languageIso]?.translation
            )
            val baseVariants = parsePlurals(
                key = keyName,
                value = translations[baseLanguageIso]?.translation
            )
            LokExString.Plural(
                key = keyName,
                value = translationVariants?.mapValues { (quantity, variant) ->
                    variant.copy(
                        value = processTranslation(
                            key = keyName,
                            translation = variant.value,
                            base = baseVariants?.get(quantity)?.value,
                            exportEmptyAs = exportEmptyAs
                        ) ?: return null.also {
                            debug(tag, "Skipping $keyName, quantity $quantity is null")
                        }
                    )
                } ?: return null.also {
                    debug(tag, "Skipping $keyName, translations is null")
                }
            )
        } else {
            LokExString.Simple(
                key = keyName,
                value = processTranslation(
                    key = keyName,
                    translation = translations[languageIso]?.translation,
                    base = translations[baseLanguageIso]?.translation,
                    exportEmptyAs = exportEmptyAs
                ) ?: return null.also {
                    debug(tag, "Skipping $keyName, translation is null")
                }
            )
        }
    }

    private fun processTranslation(
        key: String,
        translation: String?,
        base: String?,
        exportEmptyAs: LokExConfig.ExportEmptyAs
    ) = translation?.takeIf { it.isNotEmpty() } ?: when (exportEmptyAs) {
        Skip -> null
        Base -> base ?: "".also {
            debug(tag, "Exporting empty as base, but base of $key is null. Using empty instead.")
        }

        Empty -> ""
    }

    private fun parsePlurals(key: String, value: String?) = try {
        if (value.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString<Map<String, String>>(value).mapValues { (k, s) ->
                LokExString.Plural.Variant(
                    quantity = k,
                    value = s
                )
            }
        }
    } catch (e: Exception) {
        critical(tag = tag, message = "Failed to process plurals $key: $value", throwable = e)
        null
    }

    private fun performExports(
        configFile: File,
        exports: Map<LokExConfig.Export, List<LokExString>>
    ) {
        info(tag, "Running exports:")

        exports.forEach { (export, strings) ->
            performExport(
                root = configFile.parentFile.absoluteFile,
                export = export,
                strings = strings
            )
        }
    }
}