package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class SpecialAlternateCharactersTest {

    @Test
    fun halfSpacePopupDefaultsToTatweelAndKeepsJoinerAlternate() {
        assertEquals(listOf("\u0640", "\u200D"), halfSpaceAlternates.characters)
        assertEquals("\u0640", halfSpaceAlternates.characters[halfSpaceAlternates.defaultIndex])
    }
}
