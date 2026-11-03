package com.pejmands.pejkeyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftStateTest {

    @Test
    fun singleTapEnablesOneShotShift() {
        val state = ShiftState(doubleTapTimeoutMs = 300)

        state.onShiftTap(nowMillis = 1_000)

        assertTrue(state.isShifted)
        assertFalse(state.isCapsLock)
    }

    @Test
    fun committingCharacterConsumesOneShotShift() {
        val state = ShiftState(doubleTapTimeoutMs = 300)
        state.onShiftTap(nowMillis = 1_000)

        val consumed = state.onCharacterCommitted()

        assertTrue(consumed)
        assertFalse(state.isShifted)
        assertFalse(state.isCapsLock)
    }

    @Test
    fun doubleTapEnablesCapsLock() {
        val state = ShiftState(doubleTapTimeoutMs = 300)
        state.onShiftTap(nowMillis = 1_000)

        state.onShiftTap(nowMillis = 1_200)

        assertTrue(state.isShifted)
        assertTrue(state.isCapsLock)
        assertFalse(state.onCharacterCommitted())
        assertTrue(state.isCapsLock)
    }

    @Test
    fun tappingShiftWhileCapsLockIsActiveDisablesIt() {
        val state = ShiftState(doubleTapTimeoutMs = 300)
        state.onShiftTap(nowMillis = 1_000)
        state.onShiftTap(nowMillis = 1_100)

        state.onShiftTap(nowMillis = 1_200)

        assertFalse(state.isShifted)
        assertFalse(state.isCapsLock)
    }

    @Test
    fun tapOutsideDoubleTapTimeoutDoesNotEnableCapsLock() {
        val state = ShiftState(doubleTapTimeoutMs = 300)
        state.onShiftTap(nowMillis = 1_000)

        state.onShiftTap(nowMillis = 1_301)

        assertTrue(state.isShifted)
        assertFalse(state.isCapsLock)
    }

    @Test
    fun resetClearsShiftAndPendingDoubleTap() {
        val state = ShiftState(doubleTapTimeoutMs = 300)
        state.onShiftTap(nowMillis = 1_000)

        state.reset()
        state.onShiftTap(nowMillis = 1_100)

        assertTrue(state.isShifted)
        assertFalse(state.isCapsLock)
    }
}
