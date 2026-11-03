package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternateSelectionModeTest {

    @Test
    fun tapPreferenceSelectsTapMode() {
        assertEquals(
            AlternateSelectionMode.TAP,
            AlternateSelectionMode.fromPreference("tap")
        )
    }

    @Test
    fun swipePreferenceSelectsSwipeMode() {
        assertEquals(
            AlternateSelectionMode.SWIPE,
            AlternateSelectionMode.fromPreference("swipe")
        )
    }

    @Test
    fun missingOrUnknownPreferenceDefaultsToTapMode() {
        assertEquals(
            AlternateSelectionMode.TAP,
            AlternateSelectionMode.fromPreference(null)
        )
        assertEquals(
            AlternateSelectionMode.TAP,
            AlternateSelectionMode.fromPreference("unknown")
        )
    }
}
