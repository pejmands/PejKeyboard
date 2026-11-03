package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class BackspaceWordDeletionTest {

    @Test
    fun deletesPreviousWordAndItsLeadingWhitespace() {
        assertEquals(6, BackspaceWordDeletion.length("hello world"))
    }

    @Test
    fun deletesPreviousWordWhenCursorFollowsWhitespace() {
        assertEquals(6, BackspaceWordDeletion.length("hello "))
    }

    @Test
    fun deletesOnlyWordWhenNoLeadingWhitespaceExists() {
        assertEquals(5, BackspaceWordDeletion.length("hello"))
    }

    @Test
    fun includesTrailingWhitespaceInDeletion() {
        assertEquals(8, BackspaceWordDeletion.length("hello   "))
    }

    @Test
    fun deletesWhitespaceWhenNoWordPrecedesCursor() {
        assertEquals(3, BackspaceWordDeletion.length("   "))
    }

    @Test
    fun returnsZeroForEmptyText() {
        assertEquals(0, BackspaceWordDeletion.length(""))
    }
}
