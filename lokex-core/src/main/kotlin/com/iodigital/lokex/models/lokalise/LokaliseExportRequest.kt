package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
    data class LokaliseExportRequest(
    @SerialName("filter_langs") val filterLangs: List<String>,
    @SerialName("format") val format: String,
    @SerialName("placeholder_format") val placeholderFormat: String?,
    @SerialName("include_pids") val includePids: List<String>?,
    @SerialName("all_platforms") val allPlatforms: Boolean,
    @SerialName("export_empty_as") val exportEmptyAs: String,
    @SerialName("exclude_tags") val excludeTags: String,
    @SerialName("include_tags") val includeTags: String,
    )