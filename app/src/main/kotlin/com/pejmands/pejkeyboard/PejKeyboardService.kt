package com.pejmands.pejkeyboard

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.os.SystemClock
import android.view.ViewConfiguration
import android.widget.TextView

class PejKeyboardService : InputMethodService() {

    private var isPersian = true
    private var isNumberMode = false
    private val shiftState = ShiftState(ViewConfiguration.getDoubleTapTimeout().toLong())

    private lateinit var keyboard: LinearLayout

    private val isDarkMode: Boolean
        get() = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES

    private val backgroundColor: Int
        get() = if (isDarkMode) Color.rgb(30, 32, 36)
        else Color.rgb(225, 228, 234)

    private val keyColor: Int
        get() = if (isDarkMode) Color.rgb(57, 60, 67)
        else Color.rgb(250, 250, 252)

    private val specialKeyColor: Int
        get() = if (isDarkMode) Color.rgb(76, 80, 89)
        else Color.rgb(207, 212, 221)

    private val accentColor: Int
        get() = if (isDarkMode) Color.rgb(105, 153, 235)
        else Color.rgb(48, 105, 190)

    private val textColor: Int
        get() = if (isDarkMode) Color.rgb(245, 246, 248)
        else Color.rgb(35, 38, 44)

    private val persianRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج"),
        listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ"),
        listOf("ظ", "ط", "ژ", "ز", "ر", "ذ", "د", "پ", "و", "چ")
    )

    private val englishRows = listOf(
        listOf("p", "o", "i", "u", "y", "t", "r", "e", "w", "q"),
        listOf("l", "k", "j", "h", "g", "f", "d", "s", "a"),
        listOf("m", "n", "b", "v", "c", "x", "z")
    )

    private val numberRows = listOf(
        listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰"),
        listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/"),
        listOf("*", "\"", "'", ":", ";", "!", "؟", "=", "_", "،")
    )

    override fun onCreateInputView(): View {
        keyboard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            setPadding(5.dp(), 8.dp(), 5.dp(), 8.dp())
            setBackgroundColor(backgroundColor)
        }

        renderKeyboard()
        return keyboard
    }

    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        shiftState.reset()

        if (::keyboard.isInitialized) {
            renderKeyboard()
        }
    }

    private fun renderKeyboard() {
        keyboard.removeAllViews()
        keyboard.setBackgroundColor(backgroundColor)

        val rows = when {
            isNumberMode -> numberRows
            isPersian -> persianRows
            else -> englishRows
        }

        addCharacterRow(rows[0])
        addCharacterRow(rows[1])
        addThirdRow(rows[2])
        addBottomRow()
    }

    private fun addCharacterRow(keys: List<String>) {
        val row = createRow()

        keys.forEach { key ->
            val displayKey = getDisplayKey(key)

            row.addView(
                createKey(displayKey) {
                    commitCharacter(displayKey)
                },
                keyLayoutParams()
            )
        }

        keyboard.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dp()
            ).apply {
                bottomMargin = 7.dp()
            }
        )
    }

    private fun addThirdRow(keys: List<String>) {
        val row = createRow()

        if (!isPersian && !isNumberMode) {
            row.addView(
                createKey(
                    "⇧",
                    if (shiftState.isShifted || shiftState.isCapsLock) accentColor else specialKeyColor,
                    if (shiftState.isShifted || shiftState.isCapsLock) Color.WHITE else textColor
                ) {
                    handleShift()
                },
                keyLayoutParams(1.25f)
            )
        }

        keys.forEach { key ->
            val displayKey = getDisplayKey(key)

            row.addView(
                createKey(displayKey) {
                    commitCharacter(displayKey)
                },
                keyLayoutParams()
            )
        }

        row.addView(
            createKey("⌫", specialKeyColor) {
                currentInputConnection?.deleteSurroundingText(1, 0)
            },
            keyLayoutParams(1.25f)
        )

        keyboard.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dp()
            ).apply {
                bottomMargin = 7.dp()
            }
        )
    }

    private fun addBottomRow() {
        val row = createRow()

        row.addView(
            createKey(if (isNumberMode) "ABC" else "123", specialKeyColor) {
                isNumberMode = !isNumberMode
                shiftState.reset()
                renderKeyboard()
            },
            keyLayoutParams(1f)
        )

        row.addView(
            createKey(",") {
                commitKey(if (isPersian && !isNumberMode) "،" else ",")
            },
            keyLayoutParams(0.65f)
        )

        row.addView(
            createKey(if (isPersian) "FA" else "EN", specialKeyColor) {
                isPersian = !isPersian
                isNumberMode = false
                shiftState.reset()
                renderKeyboard()
            },
            keyLayoutParams(1f)
        )

        row.addView(
            createKey(" ") {
                commitKey(" ")
            },
            keyLayoutParams(2f)
        )

        row.addView(
            createKey("◂⁞▸", specialKeyColor, textColor, 16f) {
                commitKey("\u200C")
            },
            keyLayoutParams(1f)
        )

        row.addView(
            createKey(".") {
                commitKey(".")
            },
            keyLayoutParams(0.65f)
        )

        row.addView(
            createKey("↵", accentColor, Color.WHITE) {
                handleEnter()
            },
            keyLayoutParams(1f)
        )

        keyboard.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dp()
            )
        )
    }

    private fun getDisplayKey(key: String): String {
        return if (!isPersian && !isNumberMode && (shiftState.isShifted || shiftState.isCapsLock)) {
            key.uppercase()
        } else {
            key
        }
    }

    private fun commitCharacter(key: String) {
        commitKey(key)

        if (shiftState.onCharacterCommitted()) {
            renderKeyboard()
        }
    }

    private fun handleShift() {
        shiftState.onShiftTap(SystemClock.uptimeMillis())
        renderKeyboard()
    }

    private fun createRow() = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        layoutDirection = View.LAYOUT_DIRECTION_LTR
    }

    private fun createKey(
        label: String,
        color: Int = keyColor,
        foregroundColor: Int = textColor,
        size: Float = 20f,
        action: () -> Unit
    ): TextView {
        val shape = GradientDrawable().apply {
            setColor(color)
            cornerRadius = 8.dp().toFloat()
        }

        val mask = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 8.dp().toFloat()
        }

        return TextView(this).apply {
            text = label
            textSize = size
            setTextColor(foregroundColor)
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true

            background = RippleDrawable(
                ColorStateList.valueOf(
                    if (isDarkMode) Color.LTGRAY else Color.DKGRAY
                ),
                shape,
                mask
            )

            setOnClickListener { action() }
        }
    }

    private fun handleEnter() {
        val connection = currentInputConnection ?: return

        val action = currentInputEditorInfo?.imeOptions
            ?.and(EditorInfo.IME_MASK_ACTION)
            ?: EditorInfo.IME_ACTION_NONE

        if (action != EditorInfo.IME_ACTION_NONE &&
            action != EditorInfo.IME_ACTION_UNSPECIFIED
        ) {
            if (!connection.performEditorAction(action)) {
                connection.commitText("\n", 1)
            }
        } else {
            connection.commitText("\n", 1)
        }
    }

    private fun commitKey(key: String) {
        currentInputConnection?.commitText(key, 1)
    }

    private fun keyLayoutParams(weight: Float = 1f) =
        LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            weight
        ).apply {
            marginStart = 2.dp()
            marginEnd = 2.dp()
        }

    private fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()
}
