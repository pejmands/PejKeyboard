package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardFeedbackSettingsTest {

    @Test
    fun defaultsKeepSoundEnabledAndVibrationDisabled() {
        assertEquals(true, KeyboardFeedbackSettings.SOUND_DEFAULT_ENABLED)
        assertEquals(false, KeyboardFeedbackSettings.VIBRATION_DEFAULT_ENABLED)
    }

    @Test
    fun preferenceKeysAreStable() {
        assertEquals("keyboard_sound_enabled", KeyboardFeedbackSettings.KEY_SOUND_ENABLED)
        assertEquals("keyboard_vibration_enabled", KeyboardFeedbackSettings.KEY_VIBRATION_ENABLED)
    }
}
