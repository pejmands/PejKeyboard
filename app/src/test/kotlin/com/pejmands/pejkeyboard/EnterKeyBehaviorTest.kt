package com.pejmands.pejkeyboard

import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnterKeyBehaviorTest {

    @Test
    fun unspecifiedActionSendsEnterKeyEvent() {
        assertFalse(EnterKeyBehavior.shouldPerformEditorAction(0))
        assertFalse(
            EnterKeyBehavior.shouldPerformEditorAction(EditorInfo.IME_ACTION_UNSPECIFIED)
        )
    }

    @Test
    fun noneActionSendsEnterKeyEvent() {
        assertFalse(
            EnterKeyBehavior.shouldPerformEditorAction(EditorInfo.IME_ACTION_NONE)
        )
    }

    @Test
    fun explicitEditorActionsArePerformed() {
        val actions = listOf(
            EditorInfo.IME_ACTION_GO,
            EditorInfo.IME_ACTION_SEARCH,
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_PREVIOUS
        )

        actions.forEach { action ->
            assertTrue(EnterKeyBehavior.shouldPerformEditorAction(action))
        }
    }

    @Test
    fun noEnterActionFlagForcesEnterKeyEvent() {
        val imeOptions = EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_ENTER_ACTION

        assertFalse(EnterKeyBehavior.shouldPerformEditorAction(imeOptions))
    }
}
