package com.iodigital.lokex.serializer

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
internal val DefaultJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
    decodeEnumsCaseInsensitive = true
}