package com.pejmands.pejkeyboard

internal enum class AlternateSelectionMode(val preferenceValue: String) {
    TAP("tap"),
    SWIPE("swipe");

    companion object {
        fun fromPreference(value: String?): AlternateSelectionMode =
            entries.firstOrNull { it.preferenceValue == value } ?: TAP
    }
}
