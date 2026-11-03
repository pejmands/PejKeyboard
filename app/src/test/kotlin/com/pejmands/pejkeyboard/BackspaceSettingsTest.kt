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
    fun defaultsPreserveCurrentBackspaceBehavior() {
        assertEquals(500, BackspaceSettings.HOLD_DURATION_DEFAULT_MS)
        assertEquals(180, BackspaceSettings.REPEAT_INTERVAL_DEFAULT_MS)
    }
}
