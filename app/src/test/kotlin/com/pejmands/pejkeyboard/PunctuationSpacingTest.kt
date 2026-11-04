package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PunctuationSpacingTest {

    @Test
    fun supportsOnlyConfiguredPunctuation() {
        listOf(".", "،", "؛", "؟", "!", ":").forEach {
            assertTrue(PunctuationSpacing.isSupportedPunctuation(it))
        }
        listOf(",", ";", "?", "a", " ").forEach {
            assertFalse(PunctuationSpacing.isSupportedPunctuation(it))
        }
    }

    @Test
    fun removesWhitespaceBeforePunctuationAndAddsOneAfterIt() {
        val edit = PunctuationSpacing.createInsertion("هوا بارانی است   ", ".")

        assertEquals(3, edit.charactersToDelete)
        assertEquals(". ", edit.text)
    }

    @Test
    fun doesNotDeleteLineBreaksBeforePunctuation() {
        val edit = PunctuationSpacing.createInsertion("متن\n", ".")

        assertEquals(0, edit.charactersToDelete)
        assertEquals(". ", edit.text)
    }

    @Test
    fun addsSpaceWhenPunctuationIsInsertedBetweenWords() {
        val edit = PunctuationSpacing.createInsertion("هوا بارانی است", "؟")

        assertEquals(0, edit.charactersToDelete)
        assertEquals("؟ ", edit.text)
    }

    @Test
    fun doesNotDeleteTextBeforeAnActiveSelection() {
        val edit = PunctuationSpacing.createInsertion("هوا بارانی است ", "!", hasSelection = true)

        assertEquals(0, edit.charactersToDelete)
        assertEquals("! ", edit.text)
    }

    @Test
    fun identifiesOnlyTheAutomaticallyInsertedTrailingSpace() {
        assertTrue(PunctuationSpacing.hasAutoInsertedSpace("متن. ", "."))
        assertFalse(PunctuationSpacing.hasAutoInsertedSpace("متن.  ", "."))
        assertFalse(PunctuationSpacing.hasAutoInsertedSpace("متن؟ ", "."))
    }

    @Test
    fun settingIsEnabledByDefault() {
        assertTrue(PunctuationSpacingSettings.ENABLED_DEFAULT)
    }
}
