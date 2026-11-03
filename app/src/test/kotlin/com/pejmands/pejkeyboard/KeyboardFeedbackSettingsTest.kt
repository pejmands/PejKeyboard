package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardFeedbackSettingsTest {

    @Test
    fun intensityIsClampedAndRoundedToStep() {
        assertEquals(0, KeyboardFeedbackSettings.normalizeIntensity(-10))
        assertEquals(35, KeyboardFeedbackSettings.normalizeIntensity(38))
        assertEquals(100, KeyboardFeedbackSettings.normalizeIntensity(120))
    }

    @Test
    fun soundVolumeTracksIntensity() {
        assertEquals(0f, KeyboardFeedbackSettings.soundVolume(0))
        assertEquals(0.5f, KeyboardFeedbackSettings.soundVolume(50))
        assertEquals(1f, KeyboardFeedbackSettings.soundVolume(100))
    }

    @Test
    fun vibrationIntensityIncreasesAmplitudeAndDuration() {
        assertEquals(1, KeyboardFeedbackSettings.vibrationAmplitude(0))
        assertEquals(127, KeyboardFeedbackSettings.vibrationAmplitude(50))
        assertEquals(255, KeyboardFeedbackSettings.vibrationAmplitude(100))
        assertEquals(8L, KeyboardFeedbackSettings.vibrationDuration(0))
        assertEquals(19L, KeyboardFeedbackSettings.vibrationDuration(50))
        assertEquals(30L, KeyboardFeedbackSettings.vibrationDuration(100))
    }

    @Test
    fun defaultsAreMediumSoundAndDisabledVibration() {
        assertEquals(50, KeyboardFeedbackSettings.SOUND_DEFAULT_INTENSITY)
        assertEquals(0, KeyboardFeedbackSettings.VIBRATION_DEFAULT_INTENSITY)
    }
}
