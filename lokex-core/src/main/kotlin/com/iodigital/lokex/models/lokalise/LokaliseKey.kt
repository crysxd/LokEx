package com.iodigital.lokex.models.lokalise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseKey(
    @SerialName("base_words")
    val baseWords: Long? = null,
    @SerialName("char_limit")
    val charLimit: Long? = null,
    @SerialName("context")
    val context: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("created_at_timestamp")
    val createdAtTimestamp: Long? = null,
    @SerialName("custom_attributes")
    val customAttributes: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("filenames")
    val filenames: Filenames? = null,
    @SerialName("is_archived")
    val isArchived: Boolean? = null,
    @SerialName("is_hidden")
    val isHidden: Boolean? = null,
    @SerialName("is_plural")
    val isPlural: Boolean? = null,
    @SerialName("key_id")
    val keyId: Long,
    @SerialName("key_name")
    val keyName: KeyName? = null,
    @SerialName("modified_at")
    val modifiedAt: String? = null,
    @SerialName("modified_at_timestamp")
    val modifiedAtTimestamp: Long? = null,
    @SerialName("platforms")
    val platforms: List<String?>? = null,
    @SerialName("plural_name")
    val pluralName: String? = null,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("translations_modified_at")
    val translationsModifiedAt: String? = null,
    @SerialName("translations_modified_at_timestamp")
    val translationsModifiedAtTimestamp: Long? = null
) {
    @Serializable
    data class Filenames(
        @SerialName("android")
        val android: String? = null,
        @SerialName("ios")
        val ios: String? = null,
        @SerialName("other")
        val other: String? = null,
        @SerialName("web")
        val web: String? = null
    )

    @Serializable
    data class KeyName(
        @SerialName("android")
        val android: String? = null,
        @SerialName("ios")
        val ios: String? = null,
        @SerialName("other")
        val other: String? = null,
        @SerialName("web")
        val web: String? = null
    )
}