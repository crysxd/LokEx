package com.iodigital.lokex.models.lokex

import com.iodigital.lokex.ext.camel
import com.iodigital.lokex.ext.kebab
import com.iodigital.lokex.ext.pascal
import com.iodigital.lokex.ext.snake
import com.iodigital.lokex.models.Contextable

data class LokExPlaceholder(
    val index: Int,
    val type: String,
    val name: String,
) : Contextable{

    override fun toContext() = mapOf(
        "index" to index,
        "type" to type,
        "name" to  mapOf(
            "original" to name,
            "snake" to name.snake(),
            "camel" to name.camel(),
            "kebab" to name.kebab(),
            "pascal" to name.pascal(),
        ),
    )
}