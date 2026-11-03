package com.pejmands.pejkeyboard

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
        val selectedMode = AlternateSelectionMode.fromPreference(
            preferences.getString(KEY_ALTERNATE_SELECTION_MODE, null)
        )

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            textDirection = View.TEXT_DIRECTION_RTL
            gravity = Gravity.START
            setPadding(24.dp(), 24.dp(), 24.dp(), 24.dp())
        }

        content.addView(
            createTitle(R.string.alternate_selection_title),
            matchWidthWrapHeight().apply { bottomMargin = 16.dp() }
        )

        content.addView(
            TextView(this).apply {
                text = getString(R.string.alternate_selection_description)
                textSize = 15f
            },
            matchWidthWrapHeight().apply { bottomMargin = 16.dp() }
        )

        val tapOption = RadioButton(this).apply {
            id = View.generateViewId()
            text = getString(R.string.alternate_selection_tap)
            textSize = 16f
        }

        val swipeOption = RadioButton(this).apply {
            id = View.generateViewId()
            text = getString(R.string.alternate_selection_swipe)
            textSize = 16f
        }

        val options = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            addView(tapOption)
            addView(swipeOption)
            check(if (selectedMode == AlternateSelectionMode.SWIPE) swipeOption.id else tapOption.id)
            setOnCheckedChangeListener { _, checkedId ->
                val mode = if (checkedId == swipeOption.id) {
                    AlternateSelectionMode.SWIPE
                } else {
                    AlternateSelectionMode.TAP
                }
                preferences.edit().putString(KEY_ALTERNATE_SELECTION_MODE, mode.preferenceValue).apply()
            }
        }
        content.addView(options, matchWidthWrapHeight())

        addDivider(content)

        content.addView(
            createTitle(R.string.keyboard_gestures_title),
            matchWidthWrapHeight().apply { bottomMargin = 8.dp() }
        )
        content.addView(
            TextView(this).apply {
                text = getString(R.string.keyboard_gestures_description)
                textSize = 14f
            },
            matchWidthWrapHeight().apply { bottomMargin = 12.dp() }
        )

        content.addView(CheckBox(this).apply {
            text = getString(R.string.gesture_repeat_enabled)
            textSize = 15f
            isChecked = KeyboardGestureSettings.readRepeatEnabled(preferences)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit()
                    .putBoolean(KeyboardGestureSettings.KEY_REPEAT_ENABLED, isChecked)
                    .apply()
            }
        }, matchWidthWrapHeight().apply { bottomMargin = 12.dp() })

        addGestureActionSetting(content, preferences, KeyboardGestureDirection.LEFT, R.string.gesture_direction_left)
        addGestureActionSetting(content, preferences, KeyboardGestureDirection.RIGHT, R.string.gesture_direction_right)
        addGestureActionSetting(content, preferences, KeyboardGestureDirection.UP, R.string.gesture_direction_up)
        addGestureActionSetting(content, preferences, KeyboardGestureDirection.DOWN, R.string.gesture_direction_down)

        addDivider(content)

        content.addView(
            createTitle(R.string.keyboard_feedback_title),
            matchWidthWrapHeight().apply { bottomMargin = 12.dp() }
        )

        addFeedbackIntensitySetting(
            content,
            preferences,
            R.string.keyboard_sound_title,
            KeyboardFeedbackSettings.KEY_SOUND_INTENSITY,
            KeyboardFeedbackSettings.readSoundIntensity(preferences)
        )

        addFeedbackIntensitySetting(
            content,
            preferences,
            R.string.keyboard_vibration_title,
            KeyboardFeedbackSettings.KEY_VIBRATION_INTENSITY,
            KeyboardFeedbackSettings.readVibrationIntensity(preferences)
        )

        addDivider(content)

        content.addView(
            createTitle(R.string.backspace_settings_title),
            matchWidthWrapHeight().apply { bottomMargin = 8.dp() }
        )
        content.addView(
            TextView(this).apply {
                text = getString(R.string.backspace_settings_description)
                textSize = 14f
            },
            matchWidthWrapHeight().apply { bottomMargin = 16.dp() }
        )

        addTimingSetting(
            content, preferences,
            R.string.backspace_hold_duration_title,
            R.string.backspace_hold_duration_description,
            BackspaceSettings.KEY_HOLD_DURATION_MS,
            BackspaceSettings.HOLD_DURATION_MIN_MS,
            BackspaceSettings.HOLD_DURATION_MAX_MS,
            BackspaceSettings.HOLD_DURATION_STEP_MS,
            BackspaceSettings.HOLD_DURATION_DEFAULT_MS
        )

        addTimingSetting(
            content, preferences,
            R.string.backspace_repeat_interval_title,
            R.string.backspace_repeat_interval_description,
            BackspaceSettings.KEY_REPEAT_INTERVAL_MS,
            BackspaceSettings.REPEAT_INTERVAL_MIN_MS,
            BackspaceSettings.REPEAT_INTERVAL_MAX_MS,
            BackspaceSettings.REPEAT_INTERVAL_STEP_MS,
            BackspaceSettings.REPEAT_INTERVAL_DEFAULT_MS
        )

        addDivider(content)

        addSecondsSetting(content, preferences)

        content.addView(CheckBox(this).apply {
            text = getString(R.string.backspace_show_clear_warning)
            textSize = 15f
            isChecked = BackspaceSettings.readShowClearWarning(preferences)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit()
                    .putBoolean(BackspaceSettings.KEY_SHOW_CLEAR_WARNING, isChecked)
                    .apply()
            }
        }, matchWidthWrapHeight())

        setContentView(ScrollView(this).apply {
            isFillViewport = true
            addView(content)
        })
    }

    private fun addGestureActionSetting(
        content: LinearLayout,
        preferences: android.content.SharedPreferences,
        direction: KeyboardGestureDirection,
        titleRes: Int
    ) {
        content.addView(
            createTitle(titleRes, 16f),
            matchWidthWrapHeight().apply { bottomMargin = 4.dp() }
        )

        val actions = KeyboardGestureAction.entries
        val labels = actions.map { action ->
            getString(
                when (action) {
                    KeyboardGestureAction.MOVE_CURSOR -> R.string.gesture_action_move_cursor
                    KeyboardGestureAction.DELETE -> R.string.gesture_action_delete
                    KeyboardGestureAction.BACKSPACE -> R.string.gesture_action_backspace
                    KeyboardGestureAction.DISABLED -> R.string.gesture_action_disabled
                }
            )
        }
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                labels
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            setSelection(actions.indexOf(KeyboardGestureSettings.readAction(preferences, direction)))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    preferences.edit()
                        .putString(direction.preferenceKey, actions[position].preferenceValue)
                        .apply()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
        content.addView(spinner, matchWidthWrapHeight().apply {
            bottomMargin = 12.dp()
        })
    }

    private fun addFeedbackIntensitySetting(
        content: LinearLayout,
        preferences: android.content.SharedPreferences,
        titleRes: Int,
        preferenceKey: String,
        initialValue: Int
    ) {
        content.addView(
            createTitle(titleRes, 16f),
            matchWidthWrapHeight().apply { bottomMargin = 4.dp() }
        )

        val valueLabel = TextView(this).apply {
            text = getString(R.string.feedback_intensity_percent, initialValue)
            textSize = 14f
        }
        val seekBar = SeekBar(this).apply {
            max = KeyboardFeedbackSettings.INTENSITY_MAX / KeyboardFeedbackSettings.INTENSITY_STEP
            progress = initialValue / KeyboardFeedbackSettings.INTENSITY_STEP
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = progress * KeyboardFeedbackSettings.INTENSITY_STEP
                    valueLabel.text = getString(R.string.feedback_intensity_percent, value)
                    preferences.edit().putInt(preferenceKey, value).apply()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }

        content.addView(valueLabel, matchWidthWrapHeight())
        content.addView(seekBar, matchWidthWrapHeight().apply {
            bottomMargin = 16.dp()
        })
    }

    private fun addTimingSetting(
        content: LinearLayout,
        preferences: android.content.SharedPreferences,
        titleRes: Int,
        descriptionRes: Int,
        key: String,
        minimum: Int,
        maximum: Int,
        step: Int,
        defaultValue: Int
    ) {
        content.addView(createTitle(titleRes, 16f), matchWidthWrapHeight().apply {
            bottomMargin = 4.dp()
        })
        content.addView(TextView(this).apply {
            text = getString(descriptionRes)
            textSize = 13f
        }, matchWidthWrapHeight().apply {
            bottomMargin = 8.dp()
        })

        val initialValue = preferences.getInt(key, defaultValue).coerceIn(minimum, maximum)
        val valueLabel = TextView(this).apply {
            text = getString(R.string.setting_value_milliseconds, initialValue)
            textSize = 14f
        }
        val seekBar = SeekBar(this).apply {
            max = (maximum - minimum) / step
            progress = (initialValue - minimum) / step
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = minimum + progress * step
                    valueLabel.text = getString(R.string.setting_value_milliseconds, value)
                    preferences.edit().putInt(key, value).apply()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
        content.addView(valueLabel, matchWidthWrapHeight())
        content.addView(seekBar, matchWidthWrapHeight().apply {
            bottomMargin = 20.dp()
        })
    }

    private fun addSecondsSetting(
        content: LinearLayout,
        preferences: android.content.SharedPreferences
    ) {
        content.addView(
            createTitle(R.string.backspace_clear_delay_title, 16f),
            matchWidthWrapHeight().apply { bottomMargin = 4.dp() }
        )
        content.addView(TextView(this).apply {
            text = getString(R.string.backspace_clear_delay_description)
            textSize = 13f
        }, matchWidthWrapHeight().apply {
            bottomMargin = 8.dp()
        })

        val initialValue = BackspaceSettings.readClearDelay(preferences)
        val valueLabel = TextView(this).apply {
            text = getString(R.string.setting_value_seconds, initialValue / 1000f)
            textSize = 14f
        }
        val seekBar = SeekBar(this).apply {
            max = (BackspaceSettings.CLEAR_DELAY_MAX_MS - BackspaceSettings.CLEAR_DELAY_MIN_MS) /
                BackspaceSettings.CLEAR_DELAY_STEP_MS
            progress = (initialValue - BackspaceSettings.CLEAR_DELAY_MIN_MS) /
                BackspaceSettings.CLEAR_DELAY_STEP_MS
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = BackspaceSettings.CLEAR_DELAY_MIN_MS +
                        progress * BackspaceSettings.CLEAR_DELAY_STEP_MS
                    valueLabel.text = getString(R.string.setting_value_seconds, value / 1000f)
                    preferences.edit()
                        .putInt(BackspaceSettings.KEY_CLEAR_DELAY_MS, value)
                        .apply()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
        content.addView(valueLabel, matchWidthWrapHeight())
        content.addView(seekBar, matchWidthWrapHeight().apply {
            bottomMargin = 16.dp()
        })
    }

    private fun addDivider(content: LinearLayout) {
        content.addView(
            View(this).apply {
                setBackgroundColor(resources.getColor(android.R.color.darker_gray, theme))
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dp()
            ).apply {
                topMargin = 20.dp()
                bottomMargin = 20.dp()
            }
        )
    }

    private fun createTitle(resourceId: Int, size: Float = 20f) = TextView(this).apply {
        text = getString(resourceId)
        textSize = size
        setTextColor(resources.getColor(android.R.color.primary_text_light, theme))
    }

    private fun matchWidthWrapHeight() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    companion object {
        private const val KEY_ALTERNATE_SELECTION_MODE = "alternate_selection_mode"
    }
}
