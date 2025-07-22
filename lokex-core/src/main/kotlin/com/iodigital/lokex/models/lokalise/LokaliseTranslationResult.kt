package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseTranslationResult(
    @SerialName("project_id")
    val projectId: String? = null,
    @SerialName("translations")
    val translations: List<LokaliseTranslation>? = null
)