package com.pejmands.pejkeyboard

internal class ShiftState(
    private val doubleTapTimeoutMs: Long
) {
    var isShifted: Boolean = false
        private set

    var isCapsLock: Boolean = false
        private set

    private var lastShiftTapTime: Long? = null

    init {
        require(doubleTapTimeoutMs >= 0)
    }

    fun onShiftTap(nowMillis: Long) {
        val previousTapTime = lastShiftTapTime
        val isDoubleTap = isShifted &&
            previousTapTime != null &&
            nowMillis >= previousTapTime &&
            nowMillis - previousTapTime <= doubleTapTimeoutMs

        when {
            isCapsLock -> reset()

            isDoubleTap -> {
                isShifted = true
                isCapsLock = true
                lastShiftTapTime = null
            }

            else -> {
                isShifted = true
                lastShiftTapTime = nowMillis
            }
        }
    }

    fun onCharacterCommitted(): Boolean {
        if (!isShifted || isCapsLock) {
            return false
        }

        reset()
        return true
    }

    fun reset() {
        isShifted = false
        isCapsLock = false
        lastShiftTapTime = null
    }
}
