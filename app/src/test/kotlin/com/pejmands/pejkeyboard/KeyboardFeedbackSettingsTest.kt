package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardFeedbackSettingsTest {

    @Test
    fun intensityIsClampedAndRoundedToStep() {
        assertEquals(0, KeyboardFeedbackSettings.normalizeIntensity(-10))
        assertEquals(35, KeyboardFeedbackSettings.normalizeIntensity(38))
        assertEquals(100, KeyboardFeedbackSettings.normalizeIntensity(120))
    }

    @Test
    fun soundVolumeUsesPerceptualScaleAndPreservesOffState() {
        assertEquals(0f, KeyboardFeedbackSettings.soundVolume(0))
        assertEquals(0.2512f, KeyboardFeedbackSettings.soundVolume(50), 0.001f)
        assertEquals(1f, KeyboardFeedbackSettings.soundVolume(100))
        assertTrue(KeyboardFeedbackSettings.soundVolume(25) <
            KeyboardFeedbackSettings.soundVolume(50))
        assertTrue(KeyboardFeedbackSettings.soundVolume(50) <
            KeyboardFeedbackSettings.soundVolume(75))
    }

    @Test
    fun vibrationAmplitudeTracksIntensityAndZeroDisablesIt() {
        assertEquals(0, KeyboardFeedbackSettings.vibrationAmplitude(0))
        assertEquals(127, KeyboardFeedbackSettings.vibrationAmplitude(50))
        assertEquals(255, KeyboardFeedbackSettings.vibrationAmplitude(100))
    }

    @Test
    fun vibrationDurationUsesAmplitudeWhenAvailableAndWiderFallbackOtherwise() {
        assertEquals(0L, KeyboardFeedbackSettings.vibrationDuration(0, false))
        assertEquals(34L, KeyboardFeedbackSettings.vibrationDuration(50, false))
        assertEquals(60L, KeyboardFeedbackSettings.vibrationDuration(100, false))
        assertEquals(18L, KeyboardFeedbackSettings.vibrationDuration(25, true))
        assertEquals(18L, KeyboardFeedbackSettings.vibrationDuration(100, true))
    }

    @Test
    fun defaultsAreMediumSoundAndDisabledVibration() {
        assertEquals(50, KeyboardFeedbackSettings.SOUND_DEFAULT_INTENSITY)
        assertEquals(0, KeyboardFeedbackSettings.VIBRATION_DEFAULT_INTENSITY)
    }
}
