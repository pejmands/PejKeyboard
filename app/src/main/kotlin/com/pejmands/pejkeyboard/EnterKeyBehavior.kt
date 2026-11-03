package com.pejmands.pejkeyboard

import android.view.inputmethod.EditorInfo

internal object EnterKeyBehavior {

    fun shouldPerformEditorAction(imeOptions: Int): Boolean {
        val action = imeOptions and EditorInfo.IME_MASK_ACTION
        val noEnterAction = imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0

        return action != EditorInfo.IME_ACTION_NONE &&
                action != EditorInfo.IME_ACTION_UNSPECIFIED &&
                !noEnterAction
    }
}
