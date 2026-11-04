package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class SelectionAwareDeletionTest {

    @Test
    fun deletesSelectedTextInsteadOfSurroundingText() {
        var selectionDeleted = false
        var fallbackCalled = false

        val result = deleteSelectionOrFallback(
            selectedText = "selected",
            deleteSelection = {
                selectionDeleted = true
                true
            },
            deleteFallback = {
                fallbackCalled = true
                true
            }
        )

        assertEquals(true, result)
        assertEquals(true, selectionDeleted)
        assertEquals(false, fallbackCalled)
    }

    @Test
    fun usesFallbackWhenThereIsNoSelection() {
        var fallbackCalled = false

        val result = deleteSelectionOrFallback(
            selectedText = null,
            deleteSelection = { false },
            deleteFallback = {
                fallbackCalled = true
                true
            }
        )

        assertEquals(true, result)
        assertEquals(true, fallbackCalled)
    }

    @Test
    fun usesFallbackWhenSelectedTextIsEmpty() {
        var fallbackCalled = false

        val result = deleteSelectionOrFallback(
            selectedText = "",
            deleteSelection = { false },
            deleteFallback = {
                fallbackCalled = true
                true
            }
        )

        assertEquals(true, result)
        assertEquals(true, fallbackCalled)
    }
}
