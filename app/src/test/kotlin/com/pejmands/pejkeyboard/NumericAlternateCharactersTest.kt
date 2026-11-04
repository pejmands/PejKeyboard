package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NumericAlternateCharactersTest {

    @Test
    fun definesTheRequestedAlternativesForEveryPersianDigit() {
        val expected = mapOf(
            "۱" to listOf("1", "½", "⅓", "¼", "⅕", "⅙", "⅐", "¹"),
            "۲" to listOf("2", "⅔", "⅖", "²"),
            "۳" to listOf("3", "¾", "⅗", "⅜", "³"),
            "۴" to listOf("4", "⅘", "⁴"),
            "۵" to listOf("5", "⅚", "⅝", "⁵"),
            "۶" to listOf("6", "⁶"),
            "۷" to listOf("7", "⅞", "⁷"),
            "۸" to listOf("8", "⁸", "∞"),
            "۹" to listOf("9", "⁹", "⁽"),
            "۰" to listOf("0", "⁰", "⁾", "°")
        )

        assertEquals(expected.keys, NumericAlternateCharacters.byKey.keys)
        expected.forEach { (key, characters) ->
            val alternateSet = NumericAlternateCharacters.byKey.getValue(key)
            assertEquals(characters, alternateSet.characters)
            assertEquals(0, alternateSet.defaultIndex)
        }
    }

    @Test
    fun everyNumericPopupFitsWithinTheEightOptionLimit() {
        assertTrue(
            NumericAlternateCharacters.byKey.values.all {
                it.limitedTo(MAX_ALTERNATE_POPUP_OPTIONS).characters.size <= 8
            }
        )
    }

    @Test
    fun limitingAnAlternateSetPreservesItsDefaultCharacter() {
        val alternateSet = AlternateCharacterSet(
            characters = (1..10).map(Int::toString),
            defaultIndex = 9
        )

        val limited = alternateSet.limitedTo(8)

        assertEquals(listOf("1", "2", "3", "4", "5", "6", "7", "10"), limited.characters)
        assertEquals(7, limited.defaultIndex)
        assertEquals("10", limited.characters[limited.defaultIndex])
    }

    @Test
    fun doesNotChangeAlternateSetsThatAlreadyFit() {
        val alternateSet = AlternateCharacterSet(listOf("a", "b", "c"), 1)

        assertEquals(alternateSet, alternateSet.limitedTo(8))
    }
}
