package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal enum class KeyboardFeedbackLevel(val preferenceValue: String) {
    OFF("off"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    companion object {
        fun fromPreference(value: String?): KeyboardFeedbackLevel =
            entries.firstOrNull { it.preferenceValue == value } ?: MEDIUM
    }
}

internal object KeyboardFeedbackSettings {

    const val KEY_SOUND_LEVEL = "keyboard_sound_level"
    const val KEY_VIBRATION_LEVEL = "keyboard_vibration_level"

    private const val LEGACY_KEY_SOUND_ENABLED = "keyboard_sound_enabled"
    private const val LEGACY_KEY_VIBRATION_ENABLED = "keyboard_vibration_enabled"

    val SOUND_DEFAULT = KeyboardFeedbackLevel.MEDIUM
    val VIBRATION_DEFAULT = KeyboardFeedbackLevel.OFF

    fun readSoundLevel(preferences: SharedPreferences): KeyboardFeedbackLevel =
        readLevel(preferences, KEY_SOUND_LEVEL, SOUND_DEFAULT, LEGACY_KEY_SOUND_ENABLED)

    fun readVibrationLevel(preferences: SharedPreferences): KeyboardFeedbackLevel =
        readLevel(preferences, KEY_VIBRATION_LEVEL, VIBRATION_DEFAULT, LEGACY_KEY_VIBRATION_ENABLED)

    private fun readLevel(
        preferences: SharedPreferences,
        key: String,
        default: KeyboardFeedbackLevel,
        legacyKey: String
    ): KeyboardFeedbackLevel {
        if (preferences.contains(key)) {
            return KeyboardFeedbackLevel.fromPreference(preferences.getString(key, null))
        }

        if (preferences.contains(legacyKey)) {
            return if (preferences.getBoolean(legacyKey, false)) {
                KeyboardFeedbackLevel.MEDIUM
            } else {
                KeyboardFeedbackLevel.OFF
            }
        }

        return default
    }
}
