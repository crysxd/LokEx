package com.iodigital.lokex.models.lokex

import com.iodigital.lokex.models.lokalise.LokaliseKey
import com.iodigital.lokex.models.lokalise.LokaliseTranslation

data class LokExStringCache(
    val projectId: String,
    val strings: List<Entry>,
    val baseLanguageIso: String,
) {
    data class Entry(
        val key: LokaliseKey,
        val translations: List<LokaliseTranslation>,
    )
}