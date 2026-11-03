package com.pejmands.pejkeyboard

import android.content.SharedPreferences

internal enum class KeyboardGestureDirection(val preferenceKey: String) {
    LEFT("keyboard_gesture_left"),
    RIGHT("keyboard_gesture_right"),
    UP("keyboard_gesture_up"),
    DOWN("keyboard_gesture_down")
}

internal enum class KeyboardGestureAction(val preferenceValue: String) {
    MOVE_CURSOR("cursor"),
    DELETE("delete"),
    BACKSPACE("backspace"),
    DISABLED("off");

    companion object {
        fun fromPreference(value: String?): KeyboardGestureAction =
            entries.firstOrNull { it.preferenceValue == value } ?: MOVE_CURSOR
    }
}

internal object KeyboardGestureSettings {
    const val KEY_REPEAT_ENABLED = "keyboard_gesture_repeat_enabled"

    fun readAction(
        preferences: SharedPreferences,
        direction: KeyboardGestureDirection
    ): KeyboardGestureAction =
        KeyboardGestureAction.fromPreference(
            preferences.getString(direction.preferenceKey, null)
        )

    fun isRepeatEnabled(value: Boolean?): Boolean = value ?: true

    fun readRepeatEnabled(preferences: SharedPreferences): Boolean =
        isRepeatEnabled(preferences.getBoolean(KEY_REPEAT_ENABLED, true))
}
