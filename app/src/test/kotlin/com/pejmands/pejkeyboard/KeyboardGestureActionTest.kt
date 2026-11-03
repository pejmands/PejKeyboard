package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @Test
    fun reversingDirectionDoesNotRequireReturningToTheGestureOrigin() {
        val tracker = GestureDirectionTracker(touchSlop = 3f, stepDistance = 10f)

        tracker.addMovement(0f, -4f)
        val upwardStep = tracker.addMovement(0f, -10f)
        assertEquals(KeyboardGestureDirection.UP, upwardStep?.direction)
        assertEquals(1, upwardStep?.steps)

        val directionChange = tracker.addMovement(0f, 4f)
        assertNotNull(directionChange)
        assertEquals(KeyboardGestureDirection.DOWN, directionChange?.direction)
        assertEquals(true, directionChange?.directionChanged)

        val downwardStep = tracker.addMovement(0f, 6f)
        assertEquals(KeyboardGestureDirection.DOWN, downwardStep?.direction)
        assertEquals(1, downwardStep?.steps)
    }

    @Test
    fun smallOppositeMovementsDoNotChangeDirection() {
        val tracker = GestureDirectionTracker(touchSlop = 3f, stepDistance = 10f)

        tracker.addMovement(0f, -4f)
        assertNull(tracker.addMovement(0f, 1f))
        assertNull(tracker.addMovement(0f, 1f))
        val directionChange = tracker.addMovement(0f, 2f)

        assertNotNull(directionChange)
        assertEquals(KeyboardGestureDirection.DOWN, directionChange?.direction)
        assertEquals(true, directionChange?.directionChanged)
    }
}
