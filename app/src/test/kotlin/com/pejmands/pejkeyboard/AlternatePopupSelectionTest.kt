package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternatePopupSelectionTest {

    @Test
    fun selectsOptionByHorizontalPosition() {
        val bounds = listOf(0 to 44, 44 to 88, 88 to 132)

        assertEquals(0, findAlternateOptionIndex(20f, bounds))
        assertEquals(1, findAlternateOptionIndex(65f, bounds))
        assertEquals(2, findAlternateOptionIndex(110f, bounds))
    }

    @Test
    fun returnsNoSelectionOutsidePopupWidth() {
        val bounds = listOf(0 to 44, 44 to 88)

        assertEquals(-1, findAlternateOptionIndex(-1f, bounds))
        assertEquals(-1, findAlternateOptionIndex(88f, bounds))
    }

    @Test
    fun usesHalfOpenBoundsAtOptionEdges() {
        val bounds = listOf(0 to 44, 44 to 88)

        assertEquals(0, findAlternateOptionIndex(43f, bounds))
        assertEquals(1, findAlternateOptionIndex(44f, bounds))
    }
}
