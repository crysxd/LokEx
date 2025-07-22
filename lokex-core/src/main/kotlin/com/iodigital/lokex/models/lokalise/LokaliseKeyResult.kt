package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseKeyResult(
    @SerialName("keys")
    val keys: List<LokaliseKey>? = null,
    @SerialName("project_id")
    val projectId: String? = null
)