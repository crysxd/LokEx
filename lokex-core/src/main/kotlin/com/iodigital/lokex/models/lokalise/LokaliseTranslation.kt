package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseTranslation(
    @SerialName("is_reviewed")
    val isReviewed: Boolean? = null,
    @SerialName("is_unverified")
    val isUnverified: Boolean? = null,
    @SerialName("key_id")
    val keyId: Long,
    @SerialName("language_iso")
    val languageIso: String,
    @SerialName("modified_at")
    val modifiedAt: String? = null,
    @SerialName("modified_at_timestamp")
    val modifiedAtTimestamp: Long? = null,
    @SerialName("modified_by")
    val modifiedBy: Long? = null,
    @SerialName("modified_by_email")
    val modifiedByEmail: String? = null,
    @SerialName("reviewed_by")
    val reviewedBy: Long? = null,
    @SerialName("segment_number")
    val segmentNumber: Long? = null,
    @SerialName("task_id")
    val taskId: Long? = null,
    @SerialName("translation")
    val translation: String? = null,
    @SerialName("translation_id")
    val translationId: Long? = null,
    @SerialName("words")
    val words: Long? = null
)