package com.iodigital.lokex.models.lokex

import com.iodigital.lokex.ext.camel
import com.iodigital.lokex.ext.kebab
import com.iodigital.lokex.ext.pascal
import com.iodigital.lokex.ext.snake
import com.iodigital.lokex.models.Contextable

sealed class LokExString(
    open val key: String,
) : Contextable {

    protected abstract val baseValue: String

    protected fun baseContext() = mutableMapOf<String, Any>(
        "name" to mapOf(
            "original" to key,
            "snake" to key.snake(),
            "camel" to key.camel(),
            "kebab" to key.kebab(),
            "pascal" to key.pascal(),
        ),
    ).also { map ->
        map["placeholders"] = findPlaceholders()
    }.toMap()

    data class Simple(
        override val key: String,
        val value: String,
    ) : LokExString(
        key = key
    ) {
        override val baseValue = value

        override fun toContext() = baseContext() + mapOf(
            "value" to value.processPlaceholders(),
        )
    }

    data class Plural(
        override val key: String,
        val value: Map<String, Variant>,
    ) : LokExString(
        key = key
    ) {
        override val baseValue = value.values.first().value

        override fun toContext() = baseContext() + mapOf(
            "variants" to value.mapValues { (_, v) ->
                mapOf(
                    "quantity" to v.quantity,
                    "value" to v.value.processPlaceholders()
                )
            }
        )

        data class Variant(
            val quantity: String,
            val value: String,
        )
    }

    private fun findPlaceholders(): List<Map<String, Any>> {
        val placeholders = placeholderRegex.findAll(baseValue).toList()

        return placeholders.map { placeholder ->
            val index = placeholder.groups["index"]?.value
            LokExPlaceholder(
                index = if (placeholders.size > 1) {
                    requireNotNull(index?.toInt()) {
                        "'$key' contains more than one placeholder, you must specify placeholders with index, e.g.: %0\$s %1\$d"
                    }
                } else {
                    0
                },
                type = when (placeholder.groups["type"]?.value?.last()) {
                    'd', 'i', 'u' -> "Int"
                    'f', 'F' -> "Double"
                    'x', 'X' -> "Int"
                    'c' -> "Char"
                    else -> "String"
                },
                name = placeholder.groups["name"]?.value ?: "p$index"
            )
        }.toList().sortedBy { it.index }.map { it.toContext() }
    }
}

private val placeholderRegex = Regex(
    pattern = "\\[\\x25(?:(?<index>[1-9]\\d*)\\\$|\\(([^)]+)\\))?(\\+)?(0|'[^\$])?(-)?(\\d+)?(?:\\.(\\d+))?(?<type>[b-fiosuxX])(:(?<name>[a-zA-Z_0-9]+))?]"
)

private fun String.processPlaceholders() =
    placeholderRegex.replace(this) { it.value.removeSurrounding("[", "]").split(":").first() }