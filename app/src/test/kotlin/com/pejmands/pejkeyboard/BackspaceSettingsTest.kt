package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class BackspaceSettingsTest {

    @Test
    fun holdDurationIsClampedToSupportedRange() {
        assertEquals(BackspaceSettings.HOLD_DURATION_MIN_MS, BackspaceSettings.normalizeHoldDuration(Int.MIN_VALUE))
        assertEquals(BackspaceSettings.HOLD_DURATION_MAX_MS, BackspaceSettings.normalizeHoldDuration(Int.MAX_VALUE))
    }

    @Test
    fun repeatIntervalIsClampedToSupportedRange() {
        assertEquals(BackspaceSettings.REPEAT_INTERVAL_MIN_MS, BackspaceSettings.normalizeRepeatInterval(Int.MIN_VALUE))
        assertEquals(BackspaceSettings.REPEAT_INTERVAL_MAX_MS, BackspaceSettings.normalizeRepeatInterval(Int.MAX_VALUE))
    }

    @Test
    fun clearDelayIsClampedToSupportedRange() {
        assertEquals(BackspaceSettings.CLEAR_DELAY_MIN_MS, BackspaceSettings.normalizeClearDelay(Int.MIN_VALUE))
        assertEquals(BackspaceSettings.CLEAR_DELAY_MAX_MS, BackspaceSettings.normalizeClearDelay(Int.MAX_VALUE))
    }

    @Test
    fun defaultsPreserveCurrentBackspaceBehavior() {
        assertEquals(500, BackspaceSettings.HOLD_DURATION_DEFAULT_MS)
        assertEquals(180, BackspaceSettings.REPEAT_INTERVAL_DEFAULT_MS)
        assertEquals(3000, BackspaceSettings.CLEAR_DELAY_DEFAULT_MS)
        assertEquals(true, BackspaceSettings.SHOW_CLEAR_WARNING_DEFAULT)
    }
}
