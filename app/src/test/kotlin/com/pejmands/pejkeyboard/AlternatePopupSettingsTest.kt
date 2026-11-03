package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternatePopupSettingsTest {

    @Test
    fun holdDurationIsClampedToSupportedRange() {
        assertEquals(
            AlternatePopupSettings.HOLD_DURATION_MIN_MS,
            AlternatePopupSettings.normalizeHoldDuration(Int.MIN_VALUE)
        )
        assertEquals(
            AlternatePopupSettings.HOLD_DURATION_MAX_MS,
            AlternatePopupSettings.normalizeHoldDuration(Int.MAX_VALUE)
        )
    }

    @Test
    fun defaultPreservesStandardLongPressDuration() {
        assertEquals(500, AlternatePopupSettings.HOLD_DURATION_DEFAULT_MS)
    }
}
