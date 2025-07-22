package com.iodigital.lokex.models.lokex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokExConfig(
    val exports: List<Export>,
) {

    @Serializable
    data class Export(
        val language: String,
        val platform: Platform,
        val templatePath: String,
        val destinationPath: String,
        val exportEmptyAs: ExportEmptyAs,
        val filterTags: String = "",
        val placeholderFormat: String = "printf",
        val projectId: String,
        val templateVariables: Map<String, String> = emptyMap(),
    ) {
        val projectIds = projectId.split(",").map { it.trim() }
    }

    @Serializable
    enum class Platform {
        @SerialName("ios")
        iOS,

        @SerialName("android")
        Android,

        @SerialName("web")
        Web,

        @SerialName("other")
        Other;

        val lokaliseName get() = when(this) {
            iOS -> "ios"
            Android -> "android"
            Web -> "web"
            Other -> "other"
        }
    }

    @Serializable
    enum class ExportEmptyAs {
        @SerialName("skip")
        Skip,

        @SerialName("base")
        Base,

        @SerialName("empty")
        Empty
    }
}