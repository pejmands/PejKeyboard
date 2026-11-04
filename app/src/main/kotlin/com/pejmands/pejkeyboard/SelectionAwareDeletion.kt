package com.pejmands.pejkeyboard

internal fun deleteSelectionOrFallback(
    selectedText: CharSequence?,
    deleteSelection: () -> Boolean,
    deleteFallback: () -> Boolean
): Boolean {
    return if (!selectedText.isNullOrEmpty()) {
        deleteSelection()
    } else {
        deleteFallback()
    }
}
