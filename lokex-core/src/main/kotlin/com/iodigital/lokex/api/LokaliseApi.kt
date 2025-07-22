package com.iodigital.lokex.api

import com.iodigital.lokex.models.lokalise.LokaliseKey
import com.iodigital.lokex.models.lokalise.LokaliseKeyResult
import com.iodigital.lokex.models.lokalise.LokaliseProject
import com.iodigital.lokex.models.lokalise.LokaliseTranslation
import com.iodigital.lokex.models.lokalise.LokaliseTranslationResult
import com.iodigital.lokex.models.lokex.LokExStringCache
import com.iodigital.lokex.utils.debug
import com.iodigital.lokex.utils.status
import com.iodigital.lokex.utils.warning
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal class LokaliseApi(
    private val token: String,
    scope: CoroutineScope,
) {
    private val tag = "LokaliseApi"
    private val httpClient by lazy { HttpClientFactory.createClient(tag) }
    private val rateLimitReached = MutableStateFlow(false)
    private val activeCount = MutableStateFlow(0)
    private val completedCount = MutableStateFlow(0)

    init {
        scope.launch {
            combine(activeCount, completedCount, rateLimitReached) { active, completed, rateLimit ->
                status("$active active requests, $completed completed request" + if (rateLimit) ", waiting for rate limit" else "")
            }.collect()
        }
    }

    suspend fun exportStrings(
        projectId: String,
    ): LokExStringCache {
        val baseLanguageIso = getBaseLanguageIso(projectId = projectId)
        val keys = getAllKeys(projectId = projectId)
        val translations = getAllTranslations(projectId = projectId)
        val translationsMap = translations.groupBy { it.keyId }

        return LokExStringCache(
            projectId = projectId,
            baseLanguageIso = baseLanguageIso,
            strings = keys.map {
                LokExStringCache.Entry(
                    key = it,
                    translations = translationsMap[it.keyId] ?: emptyList()
                )
            }
        )
    }

    private suspend fun getBaseLanguageIso(
        projectId: String,
    ) = withRateLimit {
        httpClient.get {
            loakliseRequest("api2/projects/${projectId}")
        }.body<LokaliseProject>().let { project ->
            requireNotNull(project.baseLanguageIso) { "Missing base language id" }
        }
    }

    private suspend fun getAllKeys(
        projectId: String
    ) = loadAllPages<LokaliseKey, LokaliseKeyResult>(
        url = "api2/projects/${projectId}/keys",
        items = { keys }
    )

    private suspend fun getAllTranslations(
        projectId: String,
    ) = loadAllPages<LokaliseTranslation, LokaliseTranslationResult>(
        url = "api2/projects/${projectId}/translations",
        items = { translations }
    )

    private suspend inline fun <reified Item, reified Page> loadAllPages(
        url: String,
        crossinline items: Page.() -> List<Item>?
    ): List<Item> {
        val all = mutableListOf<Item>()
        var pageIndex = 1
        val loadPage: suspend () -> List<Item>? = {
            withRateLimit {
                httpClient.get {
                    loakliseRequest(url)
                    parameter("limit", 5000)
                    parameter("page", pageIndex)

                }.body<Page>().items()
            }
        }

        do {
            val page = loadPage()
            all.addAll(requireNotNull(page))
            pageIndex++
        } while (!page.isNullOrEmpty())

        return all.toList()
    }

    private suspend fun <T> withRateLimit(block: suspend () -> T): T = try {
        activeCount.update { it + 1 }
        if (rateLimitReached.value) {
            debug(tag = tag, message = "Rate limit reached, waiting for delay...")
            rateLimitReached.first { !it }
        }

        block()
    } catch (e: ClientRequestException) {
        if (e.response.status.value == 429) {
            val first = rateLimitReached.getAndUpdate { true }
            if (!first) {
                val delay = 10.seconds
                warning(tag = tag, message = "Rate limit reached, delaying for $delay")
                delay(delay)
                rateLimitReached.update { false }
            }
            withRateLimit(block)
        } else {
            throw e
        }
    } finally {
        activeCount.update { it - 1 }
        completedCount.update { it + 1 }
    }

    private fun HttpRequestBuilder.loakliseRequest(path: String) {
        url("https://api.lokalise.com/${path.removePrefix("/")}")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        header("X-Api-Token", token)
    }
}