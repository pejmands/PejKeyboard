package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternateCharacterSetTest {

    @Test
    fun keepsConfiguredDefaultCharacter() {
        val numericAlternates = AlternateCharacterSet(listOf("۰", "0"), 0)
        val diacriticAlternates = AlternateCharacterSet(
            listOf("ّ", "َ", "ِ", "ُ", "ً", "ْ", "ٰ", "ٔ"),
            2
        )

        assertEquals("۰", numericAlternates.characters[numericAlternates.defaultIndex])
        assertEquals("ِ", diacriticAlternates.characters[diacriticAlternates.defaultIndex])
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDefaultIndexOutsideAlternates() {
        AlternateCharacterSet(listOf("آ"), 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyAlternates() {
        AlternateCharacterSet(emptyList(), 0)
    }
}
