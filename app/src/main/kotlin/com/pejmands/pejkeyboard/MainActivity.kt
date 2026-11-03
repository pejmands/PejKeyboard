package com.pejmands.pejkeyboard

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
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
            TextView(this).apply {
                text = getString(R.string.alternate_selection_title)
                textSize = 20f
                setTextColor(resources.getColor(android.R.color.primary_text_light, theme))
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16.dp()
            }
        )

        content.addView(
            TextView(this).apply {
                text = getString(R.string.alternate_selection_description)
                textSize = 15f
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16.dp()
            }
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
            check(
                if (selectedMode == AlternateSelectionMode.SWIPE) {
                    swipeOption.id
                } else {
                    tapOption.id
                }
            )
            setOnCheckedChangeListener { _, checkedId ->
                val mode = if (checkedId == swipeOption.id) {
                    AlternateSelectionMode.SWIPE
                } else {
                    AlternateSelectionMode.TAP
                }
                preferences.edit()
                    .putString(KEY_ALTERNATE_SELECTION_MODE, mode.preferenceValue)
                    .apply()
            }
        }

        content.addView(
            options,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(content)
    }

    private fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()

    companion object {
        private const val SETTINGS_PREFERENCES = "keyboard_settings"
        private const val KEY_ALTERNATE_SELECTION_MODE = "alternate_selection_mode"
    }
}
