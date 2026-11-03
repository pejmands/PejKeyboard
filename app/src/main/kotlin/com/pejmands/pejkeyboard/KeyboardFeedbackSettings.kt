package com.pejmands.pejkeyboard

import android.content.SharedPreferences
import kotlin.math.pow

internal object KeyboardFeedbackSettings {

    const val KEY_SOUND_INTENSITY = "keyboard_sound_intensity"
    const val KEY_VIBRATION_INTENSITY = "keyboard_vibration_intensity"

    private const val LEGACY_KEY_SOUND_LEVEL = "keyboard_sound_level"
    private const val LEGACY_KEY_VIBRATION_LEVEL = "keyboard_vibration_level"
    private const val LEGACY_KEY_SOUND_ENABLED = "keyboard_sound_enabled"
    private const val LEGACY_KEY_VIBRATION_ENABLED = "keyboard_vibration_enabled"

    const val INTENSITY_MIN = 0
    const val INTENSITY_MAX = 100
    const val INTENSITY_STEP = 5
    const val SOUND_DEFAULT_INTENSITY = 50
    const val VIBRATION_DEFAULT_INTENSITY = 0

    private const val MIN_SOUND_LEVEL_DB = -24.0
    private const val MAX_SOUND_LEVEL_DB = 0.0
    private const val MIN_VIBRATION_DURATION_MS = 8L
    private const val MAX_VIBRATION_DURATION_MS = 60L
    private const val AMPLITUDE_CONTROLLED_VIBRATION_DURATION_MS = 18L

    fun readSoundIntensity(preferences: SharedPreferences): Int =
        readIntensity(
            preferences,
            KEY_SOUND_INTENSITY,
            SOUND_DEFAULT_INTENSITY,
            LEGACY_KEY_SOUND_LEVEL,
            LEGACY_KEY_SOUND_ENABLED
        )

    fun readVibrationIntensity(preferences: SharedPreferences): Int =
        readIntensity(
            preferences,
            KEY_VIBRATION_INTENSITY,
            VIBRATION_DEFAULT_INTENSITY,
            LEGACY_KEY_VIBRATION_LEVEL,
            LEGACY_KEY_VIBRATION_ENABLED
        )

    fun normalizeIntensity(value: Int): Int =
        (value.coerceIn(INTENSITY_MIN, INTENSITY_MAX) / INTENSITY_STEP) * INTENSITY_STEP

    fun soundVolume(intensity: Int): Float {
        val normalized = normalizeIntensity(intensity)
        if (normalized == 0) {
            return 0f
        }

        val decibels = MIN_SOUND_LEVEL_DB +
            (MAX_SOUND_LEVEL_DB - MIN_SOUND_LEVEL_DB) * normalized / INTENSITY_MAX
        return 10.0.pow(decibels / 20.0).toFloat().coerceIn(0f, 1f)
    }

    fun vibrationAmplitude(intensity: Int): Int {
        val normalized = normalizeIntensity(intensity)
        return normalized * 255 / INTENSITY_MAX
    }

    fun vibrationDuration(intensity: Int, hasAmplitudeControl: Boolean): Long {
        val normalized = normalizeIntensity(intensity)
        if (normalized == 0) {
            return 0L
        }

        return if (hasAmplitudeControl) {
            AMPLITUDE_CONTROLLED_VIBRATION_DURATION_MS
        } else {
            MIN_VIBRATION_DURATION_MS +
                normalized.toLong() * (MAX_VIBRATION_DURATION_MS - MIN_VIBRATION_DURATION_MS) /
                INTENSITY_MAX
        }
    }

    private fun readIntensity(
        preferences: SharedPreferences,
        key: String,
        default: Int,
        legacyLevelKey: String,
        legacyEnabledKey: String
    ): Int {
        if (preferences.contains(key)) {
            return normalizeIntensity(preferences.getInt(key, default))
        }

        if (preferences.contains(legacyLevelKey)) {
            val migrated = when (preferences.getString(legacyLevelKey, null)) {
                "off" -> 0
                "low" -> 25
                "medium" -> 50
                "high" -> 100
                else -> default
            }
            return migrated
        }

        if (preferences.contains(legacyEnabledKey)) {
            return if (preferences.getBoolean(legacyEnabledKey, false)) 50 else 0
        }

        return default
    }
}
