package com.pejmands.pejkeyboard

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.PopupWindow
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
        get() = if (isDarkMode) Color.rgb(31, 34, 40)
        else Color.rgb(237, 240, 245)

    private val keyColor: Int
        get() = if (isDarkMode) Color.rgb(54, 58, 67)
        else Color.rgb(252, 252, 254)

    private val specialKeyColor: Int
        get() = if (isDarkMode) Color.rgb(73, 79, 90)
        else Color.rgb(218, 224, 234)

    private val accentColor: Int
        get() = if (isDarkMode) Color.rgb(111, 157, 239)
        else Color.rgb(53, 105, 194)

    private val textColor: Int
        get() = if (isDarkMode) Color.rgb(245, 247, 250)
        else Color.rgb(39, 43, 51)

    private val keyBorderColor: Int
        get() = if (isDarkMode) Color.rgb(75, 81, 92)
        else Color.rgb(216, 221, 230)

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

    private val alternateCharacters = mapOf(
        "ا" to listOf("آ", "أ", "إ"),
        "ه" to listOf("ة", "ۀ"),
        "ی" to listOf("ي", "ى", "ئ"),
        "ک" to listOf("ك"),
        "و" to listOf("ؤ"),
        "," to listOf("،", "؛", "…"),
        "." to listOf("؟", "!", "؛", "…"),
        "◂⁞▸" to listOf("َ", "ِ", "ُ", "ّ", "ْ", "ً", "ٍ", "ٌ", "ٰ")
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
            setPadding(6.dp(), 8.dp(), 6.dp(), 8.dp())
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
                bottomMargin = 8.dp()
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
            cornerRadius = 10.dp().toFloat()
            setStroke(1.dp(), keyBorderColor)
        }

        val mask = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 10.dp().toFloat()
        }

        return TextView(this).apply {
            text = label
            textSize = size
            setTextColor(foregroundColor)
            gravity = Gravity.CENTER
            includeFontPadding = false
            isClickable = true
            isFocusable = true

            background = RippleDrawable(
                ColorStateList.valueOf(
                    if (isDarkMode) Color.rgb(190, 198, 212)
                    else Color.rgb(70, 82, 105)
                ),
                shape,
                mask
            )

            setOnClickListener { action() }

            val alternatives = alternateCharacters[label.lowercase()]
            if (!alternatives.isNullOrEmpty()) {
                setOnLongClickListener {
                    showAlternatives(this, alternatives)
                    true
                }
            }
        }
    }

    private fun showAlternatives(anchor: View, alternatives: List<String>) {
        val popupContent = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(6.dp(), 6.dp(), 6.dp(), 6.dp())
            background = GradientDrawable().apply {
                setColor(specialKeyColor)
                cornerRadius = 12.dp().toFloat()
                setStroke(1.dp(), keyBorderColor)
            }
        }

        lateinit var popup: PopupWindow

        alternatives.forEach { character ->
            val displayCharacter = if (character in listOf("َ", "ِ", "ُ", "ّ", "ْ", "ً", "ٍ", "ٌ", "ٰ")) {
                "◌$character"
            } else {
                character
            }

            popupContent.addView(
                TextView(this).apply {
                    text = displayCharacter
                    textSize = 20f
                    setTextColor(textColor)
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    minWidth = 44.dp()
                    minHeight = 44.dp()
                    setOnClickListener {
                        commitCharacter(character)
                        popup.dismiss()
                    }
                },
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    48.dp()
                )
            )
        }

        popupContent.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        popup = PopupWindow(
            popupContent,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 8.dp().toFloat()
        }

        popup.setClippingEnabled(false)
        popup.showAsDropDown(
            anchor,
            0,
            -anchor.height - popupContent.measuredHeight
        )
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
