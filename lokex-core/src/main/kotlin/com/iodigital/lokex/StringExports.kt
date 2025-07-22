package com.iodigital.lokex

import com.hubspot.jinjava.Jinjava
import com.hubspot.jinjava.JinjavaConfig
import com.iodigital.lokex.jinjava.LowercaseFilter
import com.iodigital.lokex.jinjava.XmlEscape
import com.iodigital.lokex.models.lokex.LokExConfig
import com.iodigital.lokex.models.lokex.LokExString
import com.iodigital.lokex.utils.info
import java.io.File
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.io.path.Path

private const val tag = "LokEx/Export"
private val jinjava by lazy {
    Jinjava(
        JinjavaConfig.newBuilder()
            .build()
    ).also {
        it.registerFilter(LowercaseFilter())
        it.registerFilter(XmlEscape())
    }
}

internal fun performExport(
    root: File,
    strings: List<LokExString>,
    export: LokExConfig.Export,
) {
    val context = createTemplateContext(
        strings = strings,
        export = export,
    ) + export.templateVariables
    val template = File(root, export.templatePath)
    val resolved = export.destinationPath.replace("~", System.getProperty("user.home"))
    val destination = if (resolved.startsWith("/")) {
        File(resolved)
    } else {
        File(root, resolved)
    }
    info(
        tag = tag,
        message = "  ${Path(template.absolutePath).normalize()} => ${Path(destination.absolutePath).normalize()}"
    )
    val result = jinjava.render(template.readText(), context)
    destination.parentFile.mkdirs()
    destination.writeText(result)
}

private fun createTemplateContext(
    strings: List<LokExString>,
    export: LokExConfig.Export,
) = mapOf(
    "date" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(Date()),
    "lokalise" to mapOf(
        "language" to export.language,
        "project_id" to export.projectId,
        "project_ids" to export.projectIds
    ),
    "strings" to strings.filterIsInstance<LokExString.Simple>()
        .sortedBy { it.key }
        .map { it.toContext() },
    "plurals" to strings.filterIsInstance<LokExString.Plural>()
        .sortedBy { it.key }
        .map { it.toContext() },
)