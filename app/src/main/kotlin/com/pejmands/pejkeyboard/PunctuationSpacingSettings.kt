package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal object PunctuationSpacingSettings {
    const val KEY_ENABLED = "punctuation_spacing_enabled"
    const val ENABLED_DEFAULT = true

    fun readEnabled(preferences: SharedPreferences): Boolean =
        preferences.getBoolean(KEY_ENABLED, ENABLED_DEFAULT)
}

internal data class PunctuationInsertion(
    val charactersToDelete: Int,
    val text: String
)

internal object PunctuationSpacing {
    private val supportedPunctuation = setOf(".", "،", "؛", "؟", "!", ":")

    fun isSupportedPunctuation(value: String): Boolean =
        value in supportedPunctuation

    fun createInsertion(
        textBeforeCursor: String,
        punctuation: String,
        hasSelection: Boolean = false
    ): PunctuationInsertion {
        require(isSupportedPunctuation(punctuation))

        val charactersToDelete = if (hasSelection) {
            0
        } else {
            textBeforeCursor.takeLastWhile { it.isWhitespace() && it != '\n' && it != '\r' }.length
        }

        return PunctuationInsertion(charactersToDelete, "$punctuation ")
    }

    fun hasAutoInsertedSpace(textBeforeCursor: String, punctuation: String): Boolean =
        textBeforeCursor.endsWith("$punctuation ")
}
