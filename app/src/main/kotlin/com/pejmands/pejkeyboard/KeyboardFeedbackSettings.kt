package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal object KeyboardFeedbackSettings {

    const val KEY_SOUND_ENABLED = "keyboard_sound_enabled"
    const val KEY_VIBRATION_ENABLED = "keyboard_vibration_enabled"

    const val SOUND_DEFAULT_ENABLED = true
    const val VIBRATION_DEFAULT_ENABLED = false

    fun readSoundEnabled(preferences: SharedPreferences): Boolean =
        preferences.getBoolean(KEY_SOUND_ENABLED, SOUND_DEFAULT_ENABLED)

    fun readVibrationEnabled(preferences: SharedPreferences): Boolean =
        preferences.getBoolean(KEY_VIBRATION_ENABLED, VIBRATION_DEFAULT_ENABLED)
}
