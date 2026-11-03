package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardFeedbackSettingsTest {

    @Test
    fun feedbackLevelsHaveStablePreferenceValues() {
        assertEquals("off", KeyboardFeedbackLevel.OFF.preferenceValue)
        assertEquals("low", KeyboardFeedbackLevel.LOW.preferenceValue)
        assertEquals("medium", KeyboardFeedbackLevel.MEDIUM.preferenceValue)
        assertEquals("high", KeyboardFeedbackLevel.HIGH.preferenceValue)
    }

    @Test
    fun unknownPreferenceFallsBackToMedium() {
        assertEquals(KeyboardFeedbackLevel.MEDIUM, KeyboardFeedbackLevel.fromPreference("unknown"))
        assertEquals(KeyboardFeedbackLevel.MEDIUM, KeyboardFeedbackLevel.fromPreference(null))
    }

    @Test
    fun defaultsKeepSoundAtMediumAndVibrationOff() {
        assertEquals(KeyboardFeedbackLevel.MEDIUM, KeyboardFeedbackSettings.SOUND_DEFAULT)
        assertEquals(KeyboardFeedbackLevel.OFF, KeyboardFeedbackSettings.VIBRATION_DEFAULT)
    }
}
