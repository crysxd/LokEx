package com.iodigital.lokex.models.lokalise


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LokaliseProject(
    @SerialName("base_language_id")
    val baseLanguageId: Int? = null,
    @SerialName("base_language_iso")
    val baseLanguageIso: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("created_at_timestamp")
    val createdAtTimestamp: Int? = null,
    @SerialName("created_by")
    val createdBy: Int? = null,
    @SerialName("created_by_email")
    val createdByEmail: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("project_id")
    val projectId: String? = null,
    @SerialName("project_type")
    val projectType: String? = null,
    @SerialName("settings")
    val settings: Settings? = null,
    @SerialName("statistics")
    val statistics: Statistics? = null,
    @SerialName("team_id")
    val teamId: Int? = null
) {
    @Serializable
    data class Settings(
        @SerialName("auto_toggle_unverified")
        val autoToggleUnverified: Boolean? = null,
        @SerialName("branching")
        val branching: Boolean? = null,
        @SerialName("contributor_preview_download_enabled")
        val contributorPreviewDownloadEnabled: Boolean? = null,
        @SerialName("custom_translation_statuses")
        val customTranslationStatuses: Boolean? = null,
        @SerialName("custom_translation_statuses_allow_multiple")
        val customTranslationStatusesAllowMultiple: Boolean? = null,
        @SerialName("inline_machine_translations")
        val inlineMachineTranslations: Boolean? = null,
        @SerialName("key_editing")
        val keyEditing: Boolean? = null,
        @SerialName("offline_translation")
        val offlineTranslation: Boolean? = null,
        @SerialName("per_platform_key_names")
        val perPlatformKeyNames: Boolean? = null,
        @SerialName("reviewing")
        val reviewing: Boolean? = null,
        @SerialName("segmentation")
        val segmentation: Boolean? = null
    )

    @Serializable
    data class Statistics(
        @SerialName("base_words")
        val baseWords: Int? = null,
        @SerialName("keys_total")
        val keysTotal: Int? = null,
        @SerialName("languages")
        val languages: List<Language?>? = null,
        @SerialName("progress_total")
        val progressTotal: Int? = null,
        @SerialName("qa_issues")
        val qaIssues: QaIssues? = null,
        @SerialName("qa_issues_total")
        val qaIssuesTotal: Int? = null,
        @SerialName("team")
        val team: Int? = null
    ) {
        @Serializable
        data class Language(
            @SerialName("language_id")
            val languageId: Int? = null,
            @SerialName("language_iso")
            val languageIso: String? = null,
            @SerialName("progress")
            val progress: Int? = null,
            @SerialName("words_to_do")
            val wordsToDo: Int? = null
        )

        @Serializable
        data class QaIssues(
            @SerialName("different_brackets")
            val differentBrackets: Int? = null,
            @SerialName("different_email_address")
            val differentEmailAddress: Int? = null,
            @SerialName("different_number_of_email_address")
            val differentNumberOfEmailAddress: Int? = null,
            @SerialName("different_number_of_urls")
            val differentNumberOfUrls: Int? = null,
            @SerialName("different_numbers")
            val differentNumbers: Int? = null,
            @SerialName("different_urls")
            val differentUrls: Int? = null,
            @SerialName("double_space")
            val doubleSpace: Int? = null,
            @SerialName("inconsistent_html")
            val inconsistentHtml: Int? = null,
            @SerialName("inconsistent_placeholders")
            val inconsistentPlaceholders: Int? = null,
            @SerialName("leading_whitespace")
            val leadingWhitespace: Int? = null,
            @SerialName("not_reviewed")
            val notReviewed: Int? = null,
            @SerialName("special_placeholder")
            val specialPlaceholder: Int? = null,
            @SerialName("spelling_grammar")
            val spellingGrammar: Int? = null,
            @SerialName("trailing_whitespace")
            val trailingWhitespace: Int? = null,
            @SerialName("unbalanced_brackets")
            val unbalancedBrackets: Int? = null,
            @SerialName("unverified")
            val unverified: Int? = null
        )
    }
}