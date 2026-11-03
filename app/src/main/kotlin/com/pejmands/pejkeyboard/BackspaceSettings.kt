package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal object BackspaceSettings {

    const val PREFERENCES_NAME = "keyboard_settings"

    const val KEY_HOLD_DURATION_MS = "backspace_hold_duration_ms"
    const val KEY_REPEAT_INTERVAL_MS = "backspace_repeat_interval_ms"

    const val HOLD_DURATION_MIN_MS = 300
    const val HOLD_DURATION_MAX_MS = 1000
    const val HOLD_DURATION_STEP_MS = 50
    const val HOLD_DURATION_DEFAULT_MS = 500

    const val REPEAT_INTERVAL_MIN_MS = 100
    const val REPEAT_INTERVAL_MAX_MS = 500
    const val REPEAT_INTERVAL_STEP_MS = 20
    const val REPEAT_INTERVAL_DEFAULT_MS = 180

    fun normalizeHoldDuration(value: Int): Int =
        value.coerceIn(HOLD_DURATION_MIN_MS, HOLD_DURATION_MAX_MS)

    fun normalizeRepeatInterval(value: Int): Int =
        value.coerceIn(REPEAT_INTERVAL_MIN_MS, REPEAT_INTERVAL_MAX_MS)

    fun readHoldDuration(preferences: SharedPreferences): Int =
        normalizeHoldDuration(preferences.getInt(KEY_HOLD_DURATION_MS, HOLD_DURATION_DEFAULT_MS))

    fun readRepeatInterval(preferences: SharedPreferences): Int =
        normalizeRepeatInterval(preferences.getInt(KEY_REPEAT_INTERVAL_MS, REPEAT_INTERVAL_DEFAULT_MS))
}
