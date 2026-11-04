package com.pejmands.pejkeyboard

import kotlin.math.roundToInt

internal fun buildAlternatePopupOrder(
    characterCount: Int,
    defaultIndex: Int
): List<Int> {
    require(characterCount > 0)
    require(defaultIndex in 0 until characterCount)

    val leftCount = characterCount / 2
    val leftIndices = (1..leftCount)
        .map { (defaultIndex - it + characterCount) % characterCount }
    val rightCount = characterCount - leftCount - 1
    val rightIndices = (1..rightCount)
        .map { (defaultIndex + it) % characterCount }

    return leftIndices.asReversed() + defaultIndex + rightIndices
}

internal fun findAlternateOptionIndexByDelta(
    deltaX: Float,
    defaultIndex: Int,
    characterCount: Int,
    optionWidth: Int
): Int {
    require(optionWidth > 0)

    val visualOrder = buildAlternatePopupOrder(characterCount, defaultIndex)
    val defaultSlot = visualOrder.indexOf(defaultIndex)
    val selectedSlot = (defaultSlot + (deltaX / optionWidth).roundToInt())
        .coerceIn(0, visualOrder.lastIndex)

    return visualOrder[selectedSlot]
}

internal fun calculateAlternateOptionWidth(
    maxPopupWidth: Int,
    optionCount: Int,
    horizontalPadding: Int,
    preferredWidth: Int,
    minimumWidth: Int
): Int {
    require(maxPopupWidth > 0)
    require(optionCount > 0)
    require(horizontalPadding >= 0)
    require(minimumWidth > 0)
    require(preferredWidth >= minimumWidth)

    val availableWidth = (maxPopupWidth - horizontalPadding).coerceAtLeast(1)
    return (availableWidth / optionCount).coerceIn(minimumWidth, preferredWidth)
}
