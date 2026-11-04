package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlternatePopupCharacterDisplayTest {

    @Test
    fun usesDistinctStandaloneGlyphsForFathaAndKasra() {
        assertEquals("\uFE76", alternatePopupCharacterDisplay("\u064E"))
        assertEquals("\uFE7A", alternatePopupCharacterDisplay("\u0650"))
        assertTrue(
            alternatePopupCharacterDisplay("\u064E") !=
                alternatePopupCharacterDisplay("\u0650")
        )
    }

    @Test
    fun mapsCommonArabicDiacriticsToStandaloneForms() {
        assertEquals("\uFE7C", alternatePopupCharacterDisplay("\u0651"))
        assertEquals("\uFE78", alternatePopupCharacterDisplay("\u064F"))
        assertEquals("\uFE70", alternatePopupCharacterDisplay("\u064B"))
        assertEquals("\uFE7E", alternatePopupCharacterDisplay("\u0652"))
        assertEquals("\u25CC\u0654", alternatePopupCharacterDisplay("\u0654"))
    }

    @Test
    fun keepsUnmappedDiacriticsAndOrdinaryCharactersUnchanged() {
        assertEquals("\u0670", alternatePopupCharacterDisplay("\u0670"))
        assertEquals("ا", alternatePopupCharacterDisplay("ا"))
    }

    @Test
    fun identifiesEverySpaceKeyDiacriticForLargerRendering() {
        listOf(
            "\u0651",
            "\u064E",
            "\u0650",
            "\u064F",
            "\u064B",
            "\u0652",
            "\u0670",
            "\u0654"
        ).forEach { character ->
            assertTrue(isDiacriticAlternateCharacter(character))
        }
    }
}
