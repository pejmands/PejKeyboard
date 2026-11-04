package com.pejmands.pejkeyboard

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.PopupWindow
import android.widget.TextView
import kotlin.math.abs

class PejKeyboardService : InputMethodService() {

    private var isPersian = true
    private var isNumberMode = false
    private val shiftState = ShiftState(ViewConfiguration.getDoubleTapTimeout().toLong())
    private val gestureHandler = Handler(Looper.getMainLooper())

    private var alternatePopup: PopupWindow? = null
    private var alternatePopupOptions: List<TextView> = emptyList()
    private var selectedAlternateIndex = -1
    private var lastBackspaceTapUpTime: Long? = null
    private var clearTextPopup: PopupWindow? = null
    private var clearTextCountdownRunnable: Runnable? = null

    private lateinit var keyboard: GestureKeyboardLayout

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
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("z", "x", "c", "v", "b", "n", "m")
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
        keyboard = GestureKeyboardLayout(
            this,
            isRepeatEnabled = {
                val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
                KeyboardGestureSettings.readRepeatEnabled(preferences)
            }
        ) { direction, steps, isFirstStep ->
            handleKeyboardGesture(direction, steps, isFirstStep)
        }.apply {
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
        dismissAlternatePopup()
        cancelClearTextCountdown()

        if (::keyboard.isInitialized) {
            renderKeyboard()
        }
    }

    override fun onFinishInput() {
        dismissAlternatePopup()
        cancelClearTextCountdown()
        super.onFinishInput()
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

        val backspaceKey = createKey("⌫", specialKeyColor) {}
        installBackspaceBehavior(backspaceKey)

        row.addView(
            backspaceKey,
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

        val enterLabel = getEnterKeyLabel()
        row.addView(
            createKey(enterLabel, accentColor, Color.WHITE, if (enterLabel.length > 2) 14f else 20f) {
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

            isSoundEffectsEnabled = false

            background = RippleDrawable(
                ColorStateList.valueOf(
                    if (isDarkMode) Color.rgb(190, 198, 212)
                    else Color.rgb(70, 82, 105)
                ),
                shape,
                mask
            )

            setOnClickListener {
                performKeyFeedback()
                action()
            }

            val alternatives = alternateCharacters[label.lowercase()]
            if (!alternatives.isNullOrEmpty()) {
                when (getAlternateSelectionMode()) {
                    AlternateSelectionMode.TAP -> {
                        installTapSelection(this, alternatives, action)
                    }

                    AlternateSelectionMode.SWIPE -> {
                        installSwipeSelection(this, alternatives, action)
                    }
                }
            }
        }
    }

    private fun handleKeyboardGesture(
        direction: KeyboardGestureDirection,
        steps: Int,
        isFirstStep: Boolean
    ) {
        val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
        val action = KeyboardGestureSettings.readAction(preferences, direction)
        if (action == KeyboardGestureAction.DISABLED || steps <= 0) {
            return
        }

        if (isFirstStep && action != KeyboardGestureAction.MOVE_CURSOR) {
            performKeyFeedback()
        }

        repeat(steps) {
            when (action) {
                KeyboardGestureAction.MOVE_CURSOR -> {
                    val keyCode = when (direction) {
                        KeyboardGestureDirection.LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
                        KeyboardGestureDirection.RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
                        KeyboardGestureDirection.UP -> KeyEvent.KEYCODE_DPAD_UP
                        KeyboardGestureDirection.DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
                    }
                    sendDownUpKeyEvents(keyCode)
                }

                KeyboardGestureAction.DELETE -> {
                    currentInputConnection?.deleteSurroundingTextInCodePoints(0, 1)
                }

                KeyboardGestureAction.BACKSPACE -> {
                    currentInputConnection?.deleteSurroundingTextInCodePoints(1, 0)
                }

                KeyboardGestureAction.DISABLED -> Unit
            }
        }
    }

    private fun performKeyFeedback() {
        performKeySoundFeedback()
        performKeyHapticFeedback()
    }

    private fun performKeySoundFeedback() {
        val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
        val intensity = KeyboardFeedbackSettings.readSoundIntensity(preferences)
        if (intensity == 0) {
            return
        }

        getSystemService(AudioManager::class.java)?.playSoundEffect(
            SoundEffectConstants.CLICK,
            KeyboardFeedbackSettings.soundVolume(intensity)
        )
    }

    private fun performKeyHapticFeedback() {
        val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
        val intensity = KeyboardFeedbackSettings.readVibrationIntensity(preferences)
        if (intensity == 0) {
            return
        }

        val vibrator = getSystemService(Vibrator::class.java) ?: return
        if (vibrator.hasVibrator()) {
            val hasAmplitudeControl = vibrator.hasAmplitudeControl()
            val amplitude = if (hasAmplitudeControl) {
                KeyboardFeedbackSettings.vibrationAmplitude(intensity)
            } else {
                VibrationEffect.DEFAULT_AMPLITUDE
            }
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    KeyboardFeedbackSettings.vibrationDuration(intensity, hasAmplitudeControl),
                    amplitude
                )
            )
        }
    }

    private fun getAlternateSelectionMode(): AlternateSelectionMode {
        val value = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
            .getString(KEY_ALTERNATE_SELECTION_MODE, AlternateSelectionMode.TAP.preferenceValue)
        return AlternateSelectionMode.fromPreference(value)
    }

    private fun installTapSelection(
        keyView: View,
        alternatives: List<String>,
        keyAction: () -> Unit
    ) {
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        var downX = 0f
        var downY = 0f
        var movedBeforeLongPress = false
        var longPressTriggered = false
        var movedHorizontallyAfterLongPress = false
        var longPressRunnable: Runnable? = null

        keyView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    keyView.isPressed = true
                    downX = event.rawX
                    downY = event.rawY
                    movedBeforeLongPress = false
                    longPressTriggered = false

                    val preferences = getSharedPreferences(
                        BackspaceSettings.PREFERENCES_NAME,
                        MODE_PRIVATE
                    )
                    val holdDurationMs = AlternatePopupSettings.readHoldDuration(preferences)
                    longPressRunnable = Runnable {
                        longPressTriggered = true
                        showAlternatives(keyView, alternatives, swipeSelection = false)
                    }
                    gestureHandler.postDelayed(longPressRunnable!!, holdDurationMs.toLong())
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!longPressTriggered &&
                        (abs(event.rawX - downX) > touchSlop ||
                            abs(event.rawY - downY) > touchSlop)
                    ) {
                        movedBeforeLongPress = true
                        keyView.isPressed = false
                        longPressRunnable?.let(gestureHandler::removeCallbacks)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    keyView.isPressed = false

                    if (!longPressTriggered && !movedBeforeLongPress) {
                        performKeyFeedback()
                        keyAction()
                    }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    keyView.isPressed = false
                    if (longPressTriggered) {
                        dismissAlternatePopup()
                    }
                    true
                }

                else -> true
            }
        }
    }

    private fun installSwipeSelection(
        keyView: View,
        alternatives: List<String>,
        keyAction: () -> Unit
    ) {
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        var downX = 0f
        var downY = 0f
        var movedBeforeLongPress = false
        var longPressTriggered = false
        var longPressRunnable: Runnable? = null

        keyView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    keyView.isPressed = true
                    downX = event.rawX
                    downY = event.rawY
                    movedBeforeLongPress = false
                    longPressTriggered = false
                    movedHorizontallyAfterLongPress = false
                    selectedAlternateIndex = -1

                    longPressRunnable = Runnable {
                        longPressTriggered = true
                        keyView.parent?.requestDisallowInterceptTouchEvent(true)
                        showAlternatives(keyView, alternatives, swipeSelection = true)
                    }
                    val preferences = getSharedPreferences(
                        BackspaceSettings.PREFERENCES_NAME,
                        MODE_PRIVATE
                    )
                    val holdDurationMs = AlternatePopupSettings.readHoldDuration(preferences)
                    gestureHandler.postDelayed(longPressRunnable!!, holdDurationMs.toLong())
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!longPressTriggered) {
                        if (abs(event.rawX - downX) > touchSlop ||
                            abs(event.rawY - downY) > touchSlop
                        ) {
                            movedBeforeLongPress = true
                            keyView.isPressed = false
                            longPressRunnable?.let(gestureHandler::removeCallbacks)
                        }
                    } else if (abs(event.rawX - downX) > touchSlop) {
                        movedHorizontallyAfterLongPress = true
                        updateSwipeSelection(event.rawX)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    keyView.parent?.requestDisallowInterceptTouchEvent(false)
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    keyView.isPressed = false

                    if (longPressTriggered) {
                        if (movedHorizontallyAfterLongPress) {
                            updateSwipeSelection(event.rawX)
                        }
                        val selectedCharacter = alternatives.getOrNull(selectedAlternateIndex)
                        dismissAlternatePopup()
                        if (selectedCharacter != null) {
                            performKeyFeedback()
                            commitCharacter(selectedCharacter)
                        }
                    } else if (!movedBeforeLongPress) {
                        performKeyFeedback()
                        keyAction()
                    }

                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    keyView.parent?.requestDisallowInterceptTouchEvent(false)
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    keyView.isPressed = false
                    dismissAlternatePopup()
                    true
                }

                else -> true
            }
        }
    }

    private fun showAlternatives(
        anchor: View,
        alternatives: List<String>,
        swipeSelection: Boolean
    ) {
        dismissAlternatePopup()

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

        val optionViews = alternatives.map { character ->
            val displayCharacter = if (character in DIACRITIC_CHARACTERS) {
                "◌$character"
            } else {
                character
            }

            TextView(this).apply {
                text = displayCharacter
                textSize = 20f
                setTextColor(textColor)
                gravity = Gravity.CENTER
                includeFontPadding = false
                isSoundEffectsEnabled = false
                minWidth = 44.dp()
                minHeight = 44.dp()

                if (!swipeSelection) {
                    setOnClickListener {
                        performKeyFeedback()
                        commitCharacter(character)
                        dismissAlternatePopup()
                    }
                }
            }.also { option ->
                popupContent.addView(
                    option,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        48.dp()
                    )
                )
            }
        }

        popupContent.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val visibleFrame = Rect()
        anchor.getWindowVisibleDisplayFrame(visibleFrame)
        val horizontalMargin = 8.dp()
        val popupWidth = popupContent.measuredWidth.coerceAtMost(
            (visibleFrame.width() - horizontalMargin * 2).coerceAtLeast(1)
        )
        val popupHeight = popupContent.measuredHeight

        val popupContainer = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFillViewport = false
            overScrollMode = View.OVER_SCROLL_NEVER
            clipToPadding = true
            addView(
                popupContent,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        popupContainer.measure(
            View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(popupHeight, View.MeasureSpec.EXACTLY)
        )

        val popup = PopupWindow(
            popupContainer,
            popupWidth,
            popupHeight,
            false
        ).apply {
            isTouchable = !swipeSelection
            isOutsideTouchable = !swipeSelection
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 8.dp().toFloat()
            setClippingEnabled(true)
            setIsLaidOutInScreen(true)
            setIsClippedToScreen(true)
        }

        val anchorLocation = IntArray(2)
        anchor.getLocationOnScreen(anchorLocation)

        val verticalMargin = 8.dp()
        val minLeft = visibleFrame.left + horizontalMargin
        val maxLeft = visibleFrame.right - horizontalMargin - popupWidth
        val centeredLeft = anchorLocation[0] + (anchor.width - popupWidth) / 2
        val popupLeft = centeredLeft.coerceIn(minLeft, maxLeft.coerceAtLeast(minLeft))

        val anchorTop = anchorLocation[1]
        val anchorBottom = anchorTop + anchor.height
        val spaceAbove = anchorTop - visibleFrame.top
        val spaceBelow = visibleFrame.bottom - anchorBottom
        val showAbove = spaceAbove >= popupHeight || spaceAbove >= spaceBelow
        val desiredTop = if (showAbove) {
            anchorTop - popupHeight
        } else {
            anchorBottom
        }
        val maxTop = (visibleFrame.bottom - verticalMargin - popupHeight)
            .coerceAtLeast(visibleFrame.top + verticalMargin)
        val popupTop = desiredTop.coerceIn(
            visibleFrame.top + verticalMargin,
            maxTop
        )

        popup.showAtLocation(
            anchor,
            Gravity.TOP or Gravity.LEFT,
            popupLeft,
            popupTop
        )
        alternatePopup = popup

        if (swipeSelection) {
            alternatePopupOptions = optionViews
            selectedAlternateIndex = -1
        }
    }

    private fun updateSwipeSelection(rawX: Float) {
        val optionBounds = alternatePopupOptions.map { option ->
            val location = IntArray(2)
            option.getLocationOnScreen(location)
            location[0] to (location[0] + option.width)
        }
        val nextIndex = findAlternateOptionIndex(rawX, optionBounds)

        if (nextIndex == selectedAlternateIndex) {
            return
        }

        selectedAlternateIndex = nextIndex
        alternatePopupOptions.forEachIndexed { index, option ->
            val isSelected = index == selectedAlternateIndex
            option.background = if (isSelected) {
                GradientDrawable().apply {
                    setColor(accentColor)
                    cornerRadius = 8.dp().toFloat()
                }
            } else {
                null
            }
            option.setTextColor(if (isSelected) Color.WHITE else textColor)
        }
    }

    private fun dismissAlternatePopup() {
        alternatePopup?.dismiss()
        alternatePopup = null
        alternatePopupOptions = emptyList()
        selectedAlternateIndex = -1
    }


    private fun installBackspaceBehavior(keyView: View) {
        val doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout().toLong()

        var longPressTriggered = false
        var isDoubleTapHold = false
        var longPressRunnable: Runnable? = null
        var repeatRunnable: Runnable? = null
        var repeatIntervalMs = BackspaceSettings.REPEAT_INTERVAL_DEFAULT_MS.toLong()

        keyView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                keyView.isPressed = true
            }

            if (clearTextCountdownRunnable != null &&
                event.actionMasked == MotionEvent.ACTION_DOWN
            ) {
                cancelClearTextCountdown()
                longPressTriggered = true
                return@setOnTouchListener true
            }

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    longPressTriggered = false
                    val preferences = getSharedPreferences(BackspaceSettings.PREFERENCES_NAME, MODE_PRIVATE)
                    val holdDurationMs = BackspaceSettings.readHoldDuration(preferences).toLong()
                    repeatIntervalMs = BackspaceSettings.readRepeatInterval(preferences).toLong()
                    val previousTapTime = lastBackspaceTapUpTime
                    isDoubleTapHold = previousTapTime != null &&
                            event.eventTime >= previousTapTime &&
                            event.eventTime - previousTapTime <= doubleTapTimeout

                    longPressRunnable = Runnable {
                        longPressTriggered = true
                        lastBackspaceTapUpTime = null

                        if (isDoubleTapHold) {
                            performKeyFeedback()
                            val clearPreferences = getSharedPreferences(
                                BackspaceSettings.PREFERENCES_NAME,
                                MODE_PRIVATE
                            )
                            startClearTextCountdown(
                                keyView,
                                BackspaceSettings.readClearDelay(clearPreferences).toLong(),
                                BackspaceSettings.readShowClearWarning(clearPreferences)
                            )
                        } else {
                            if (deletePreviousWord()) {
                                performKeyFeedback()
                            }
                            repeatRunnable = object : Runnable {
                                override fun run() {
                                    if (!longPressTriggered || isDoubleTapHold) {
                                        return
                                    }

                                    deletePreviousWord()
                                    gestureHandler.postDelayed(this, repeatIntervalMs)
                                }
                            }
                            gestureHandler.postDelayed(
                                repeatRunnable!!,
                                repeatIntervalMs
                            )
                        }
                    }

                    gestureHandler.postDelayed(longPressRunnable!!, holdDurationMs)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    keyView.parent?.requestDisallowInterceptTouchEvent(false)
                    keyView.isPressed = false
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    repeatRunnable?.let(gestureHandler::removeCallbacks)

                    if (clearTextCountdownRunnable != null) {
                        cancelClearTextCountdown()
                    }

                    if (!longPressTriggered) {
                        if (currentInputConnection?.deleteSurroundingText(1, 0) == true) {
                            performKeyFeedback()
                        }
                        lastBackspaceTapUpTime = event.eventTime
                    }

                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    keyView.parent?.requestDisallowInterceptTouchEvent(false)
                    keyView.isPressed = false
                    longPressRunnable?.let(gestureHandler::removeCallbacks)
                    repeatRunnable?.let(gestureHandler::removeCallbacks)
                    cancelClearTextCountdown()
                    longPressTriggered = false
                    true
                }

                else -> true
            }
        }
    }


    private fun startClearTextCountdown(
        anchor: View,
        durationMs: Long,
        showWarning: Boolean
    ) {
        cancelClearTextCountdown()

        var progressBar: ProgressBar? = null
        var countdownLabel: TextView? = null

        if (showWarning) {
            val popupContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutDirection = View.LAYOUT_DIRECTION_RTL
                setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
                background = GradientDrawable().apply {
                    setColor(specialKeyColor)
                    cornerRadius = 12.dp().toFloat()
                    setStroke(1.dp(), keyBorderColor)
                }
            }

            popupContent.addView(
                TextView(this).apply {
                    text = getString(R.string.backspace_clear_warning_title)
                    textSize = 16f
                    setTextColor(textColor)
                }, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 6.dp()
                })

            popupContent.addView(
                TextView(this).apply {
                    text = getString(R.string.backspace_clear_warning_message)
                    textSize = 13f
                    setTextColor(textColor)
                }, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8.dp()
                })

            countdownLabel = TextView(this).apply {
                text = getString(R.string.setting_value_seconds, durationMs / 1000f)
                textSize = 13f
                setTextColor(textColor)
                gravity = Gravity.CENTER
            }
            popupContent.addView(
                countdownLabel, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 4.dp()
                })

            progressBar = ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            ).apply {
                max = 1000
                progress = 0
                progressTintList = ColorStateList.valueOf(accentColor)
                progressBackgroundTintList = ColorStateList.valueOf(keyBorderColor)
            }
            popupContent.addView(
                progressBar, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    6.dp()
                )
            )

            popupContent.measure(
                View.MeasureSpec.makeMeasureSpec(260.dp(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val popup = PopupWindow(
                popupContent,
                260.dp(),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                false
            ).apply {
                isTouchable = true
                isOutsideTouchable = false
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                elevation = 8.dp().toFloat()
            }

            popup.setClippingEnabled(false)
            popup.showAsDropDown(
                anchor,
                (anchor.width - 260.dp()) / 2,
                -anchor.height - popupContent.measuredHeight
            )
            clearTextPopup = popup
        }

        val startedAt = SystemClock.uptimeMillis()
        clearTextCountdownRunnable = object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - startedAt
                val remaining = (durationMs - elapsed).coerceAtLeast(0L)

                progressBar?.progress = ((elapsed.coerceAtMost(durationMs) * 1000L) /
                        durationMs).toInt()
                countdownLabel?.text = getString(
                    R.string.setting_value_seconds,
                    remaining / 1000f
                )

                if (remaining == 0L) {
                    clearTextCountdownRunnable = null
                    clearTextPopup?.dismiss()
                    clearTextPopup = null
                    deleteAllBeforeCursor()
                } else {
                    gestureHandler.postDelayed(this, CLEAR_TEXT_COUNTDOWN_UPDATE_MS)
                }
            }
        }
        gestureHandler.post(clearTextCountdownRunnable!!)
    }

    private fun cancelClearTextCountdown() {
        clearTextCountdownRunnable?.let(gestureHandler::removeCallbacks)
        clearTextCountdownRunnable = null
        clearTextPopup?.dismiss()
        clearTextPopup = null
    }

    private fun deletePreviousWord(): Boolean {
        val connection = currentInputConnection ?: return false
        val textBeforeCursor = connection.getTextBeforeCursor(BACKSPACE_CONTEXT_LENGTH, 0)
            ?: return false
        val deleteLength = BackspaceWordDeletion.length(textBeforeCursor)

        return deleteLength > 0 && connection.deleteSurroundingText(deleteLength, 0)
    }

    private fun deleteAllBeforeCursor() {
        val connection = currentInputConnection ?: return

        repeat(MAX_BACKSPACE_DELETE_BATCHES) {
            val textBeforeCursor = connection.getTextBeforeCursor(BACKSPACE_CONTEXT_LENGTH, 0)
                ?: return
            if (textBeforeCursor.isEmpty()) {
                return
            }

            if (!connection.deleteSurroundingText(textBeforeCursor.length, 0)) {
                return
            }
        }
    }

    private fun getEnterKeyLabel(): String {
        val imeOptions = currentInputEditorInfo?.imeOptions ?: return "↵"
        if (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0) {
            return "↵"
        }

        return when (imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_SEARCH -> "⌕"
            EditorInfo.IME_ACTION_SEND -> "➤"
            EditorInfo.IME_ACTION_NEXT -> "→"
            EditorInfo.IME_ACTION_DONE -> "✓"
            EditorInfo.IME_ACTION_PREVIOUS -> "←"
            else -> "↵"
        }
    }

    private fun handleEnter() {
        cancelClearTextCountdown()
        if (currentInputConnection == null) {
            return
        }

        val imeOptions = currentInputEditorInfo?.imeOptions ?: 0
        val action = imeOptions and EditorInfo.IME_MASK_ACTION

        if (EnterKeyBehavior.shouldPerformEditorAction(imeOptions) &&
            currentInputConnection?.performEditorAction(action) == true
        ) {
            return
        }

        sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
    }

    private fun commitKey(key: String) {
        cancelClearTextCountdown()
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

    companion object {
        private const val KEY_ALTERNATE_SELECTION_MODE = "alternate_selection_mode"
        private const val BACKSPACE_CONTEXT_LENGTH = 2048
        private const val MAX_BACKSPACE_DELETE_BATCHES = 256
        private const val CLEAR_TEXT_COUNTDOWN_UPDATE_MS = 50L
        private val DIACRITIC_CHARACTERS = setOf("َ", "ِ", "ُ", "ّ", "ْ", "ً", "ٍ", "ٌ", "ٰ")
    }
}
