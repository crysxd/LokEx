package com.iodigital.lokex.api

import com.iodigital.lokex.utils.cacheDir
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.HttpHeaders
import okhttp3.Cache
import java.io.File

actual fun createPlatformClient(block: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
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