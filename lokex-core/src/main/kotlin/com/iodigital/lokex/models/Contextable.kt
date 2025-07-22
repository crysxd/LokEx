package com.iodigital.lokex.models

internal interface Contextable {
    fun toContext(): Map<String, Any>
}