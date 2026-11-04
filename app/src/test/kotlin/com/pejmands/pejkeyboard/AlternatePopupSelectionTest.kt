package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class AlternatePopupSelectionTest {

    @Test
    fun placesDefaultOptionNearTheCenterForFourAlternates() {
        assertEquals(
            listOf(2, 3, 0, 1),
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
    fun wrapsAroundWithoutDuplicatingOrOmittingEightAlternates() {
        assertEquals(
            listOf(6, 7, 0, 1, 2, 3, 4, 5),
            buildAlternatePopupOrder(characterCount = 8, defaultIndex = 2)
        )
    }

    @Test
    fun returnsEveryAlternateExactlyOnceForEveryDefaultPosition() {
        for (characterCount in 1..12) {
            val expectedIndices = (0 until characterCount).toSet()

            for (defaultIndex in 0 until characterCount) {
                val order = buildAlternatePopupOrder(characterCount, defaultIndex)

                assertEquals(characterCount, order.size)
                assertEquals(characterCount, order.toSet().size)
                assertEquals(expectedIndices, order.toSet())
                assertEquals(defaultIndex, order[characterCount / 2])
            }
        }
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
            3,
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
            2,
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

    @Test
    fun fitsEightAlternatesWithinAvailablePopupWidth() {
        assertEquals(
            43,
            calculateAlternateOptionWidth(
                maxPopupWidth = 360,
                optionCount = 8,
                horizontalPadding = 12,
                preferredWidth = 48,
                minimumWidth = 36
            )
        )
    }

    @Test
    fun keepsPreferredWidthWhenThereIsEnoughSpace() {
        assertEquals(
            48,
            calculateAlternateOptionWidth(
                maxPopupWidth = 420,
                optionCount = 8,
                horizontalPadding = 12,
                preferredWidth = 48,
                minimumWidth = 36
            )
        )
    }

    @Test
    fun usesMinimumWidthAndLeavesScrollingAsFallbackOnNarrowScreens() {
        assertEquals(
            36,
            calculateAlternateOptionWidth(
                maxPopupWidth = 240,
                optionCount = 8,
                horizontalPadding = 12,
                preferredWidth = 48,
                minimumWidth = 36
            )
        )
    }

    @Test
    fun calculatesWidthForSingleAlternate() {
        assertEquals(
            48,
            calculateAlternateOptionWidth(
                maxPopupWidth = 100,
                optionCount = 1,
                horizontalPadding = 12,
                preferredWidth = 48,
                minimumWidth = 36
            )
        )
    }

}
