package com.iodigital.lokex.api

import com.iodigital.lokex.serializer.DefaultJson
import com.iodigital.lokex.utils.cacheDir
import com.iodigital.lokex.utils.debug
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Cache
import java.io.File

internal object HttpClientFactory {

    fun createClient(tag: String) = createPlatformClient {
        expectSuccess = true
        install(HttpCache)
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    debug(tag = tag, message = message)
                }
            }
        }
        install(ContentEncoding) {
            deflate(1.0f)
            gzip(0.9f)
        }
        install(ContentNegotiation) {
            json(DefaultJson)
        }
    }
}

fun createPlatformClient(block: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    engine {
        addNetworkInterceptor {
            // KTOR adds "Content-Length: 0" to all requests....Lokalise returns 400 in this case
            val baseRequest = it.request()
            val request = baseRequest.newBuilder()
                .run {
                    if (baseRequest.header(HttpHeaders.ContentLength) == "0") {
                        removeHeader(HttpHeaders.ContentLength)
                    } else {
                        this
                    }
                }
                .build()
            it.proceed(request)
        }

        config {
            readTimeout(120, java.util.concurrent.TimeUnit.SECONDS).cache(
                Cache(
                    directory = File(cacheDir, "http"),
                    maxSize = 256L * 1024L * 1024L // 256 MiB
                )
            )
        }
    }

    block()
}