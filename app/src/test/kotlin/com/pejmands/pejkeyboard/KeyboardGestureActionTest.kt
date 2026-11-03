package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardGestureActionTest {

    @Test
    fun knownPreferencesSelectTheirActions() {
        assertEquals(KeyboardGestureAction.MOVE_CURSOR, KeyboardGestureAction.fromPreference("cursor"))
        assertEquals(KeyboardGestureAction.DELETE, KeyboardGestureAction.fromPreference("delete"))
        assertEquals(KeyboardGestureAction.BACKSPACE, KeyboardGestureAction.fromPreference("backspace"))
        assertEquals(KeyboardGestureAction.DISABLED, KeyboardGestureAction.fromPreference("off"))
    }

    @Test
    fun missingOrUnknownPreferenceDefaultsToCursorMovement() {
        assertEquals(KeyboardGestureAction.MOVE_CURSOR, KeyboardGestureAction.fromPreference(null))
        assertEquals(KeyboardGestureAction.MOVE_CURSOR, KeyboardGestureAction.fromPreference("unknown"))
    }

    @Test
    fun gestureRepeatDefaultsToEnabledWhenPreferenceIsMissing() {
        assertEquals(true, KeyboardGestureSettings.isRepeatEnabled(null))
    }

    @Test
    fun gestureRepeatUsesTheSavedPreference() {
        assertEquals(true, KeyboardGestureSettings.isRepeatEnabled(true))
        assertEquals(false, KeyboardGestureSettings.isRepeatEnabled(false))
    }

    @Test
    fun eachDirectionHasAnIndependentPreferenceKey() {
        assertEquals(
            setOf("keyboard_gesture_left", "keyboard_gesture_right", "keyboard_gesture_up", "keyboard_gesture_down"),
            KeyboardGestureDirection.entries.map { it.preferenceKey }.toSet()
        )
    }
}
