package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal object AlternatePopupSettings {

    const val KEY_HOLD_DURATION_MS = "alternate_popup_hold_duration_ms"

    const val HOLD_DURATION_MIN_MS = 300
    const val HOLD_DURATION_MAX_MS = 1000
    const val HOLD_DURATION_STEP_MS = 50
    const val HOLD_DURATION_DEFAULT_MS = 500

    fun normalizeHoldDuration(value: Int): Int =
        value.coerceIn(HOLD_DURATION_MIN_MS, HOLD_DURATION_MAX_MS)

    fun readHoldDuration(preferences: SharedPreferences): Int =
        normalizeHoldDuration(
            preferences.getInt(KEY_HOLD_DURATION_MS, HOLD_DURATION_DEFAULT_MS)
        )
}
