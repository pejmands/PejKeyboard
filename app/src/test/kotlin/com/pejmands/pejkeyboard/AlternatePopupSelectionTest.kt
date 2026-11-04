package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternatePopupSelectionTest {

    @Test
    fun placesDefaultOptionNearTheCenterForFourAlternates() {
        assertEquals(
            listOf(3, 2, 0, 1),
            buildAlternatePopupOrder(characterCount = 4, defaultIndex = 0)
        )
    }

    @Test
    fun keepsAlternatesBeforeDefaultOnItsLeftAndAfterDefaultOnItsRight() {
        assertEquals(
            listOf(0, 1, 2, 3, 4),
            buildAlternatePopupOrder(characterCount = 5, defaultIndex = 2)
        )
    }

    @Test
    fun selectsTheDefaultWithoutHorizontalMovement() {
        assertEquals(
            0,
            findAlternateOptionIndexByDelta(
                deltaX = 0f,
                defaultIndex = 0,
                characterCount = 4,
                optionWidth = 48
            )
        )
    }

    @Test
    fun movingRightSelectsTheNextConfiguredAlternate() {
        assertEquals(
            1,
            findAlternateOptionIndexByDelta(
                deltaX = 48f,
                defaultIndex = 0,
                characterCount = 4,
                optionWidth = 48
            )
        )
    }

    @Test
    fun movingLeftSelectsTheNearestAlternateOnTheLeft() {
        assertEquals(
            2,
            findAlternateOptionIndexByDelta(
                deltaX = -48f,
                defaultIndex = 0,
                characterCount = 4,
                optionWidth = 48
            )
        )
    }

    @Test
    fun clampsSelectionAtTheAvailableAlternates() {
        assertEquals(
            3,
            findAlternateOptionIndexByDelta(
                deltaX = -500f,
                defaultIndex = 0,
                characterCount = 4,
                optionWidth = 48
            )
        )
        assertEquals(
            1,
            findAlternateOptionIndexByDelta(
                deltaX = 500f,
                defaultIndex = 0,
                characterCount = 4,
                optionWidth = 48
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsAnEmptyAlternateList() {
        buildAlternatePopupOrder(characterCount = 0, defaultIndex = 0)
    }
}
