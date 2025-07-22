package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseExportResult(
    @SerialName("project_id") val projectId: String,
    @SerialName("bundle_url") val bundleUrl: String,
)