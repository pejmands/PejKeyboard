package com.pejmands.pejkeyboard

import kotlin.math.roundToInt

internal fun buildAlternatePopupOrder(
    characterCount: Int,
    defaultIndex: Int,
    leftOptionCount: Int = characterCount / 2
): List<Int> {
    require(characterCount > 0)
    require(defaultIndex in 0 until characterCount)
    require(leftOptionCount in 0 until characterCount)

    val leftIndices = (1..leftOptionCount)
        .map { (defaultIndex - it + characterCount) % characterCount }
    val rightCount = characterCount - leftOptionCount - 1
    val rightIndices = (1..rightCount)
        .map { (defaultIndex + it) % characterCount }

    return leftIndices.asReversed() + defaultIndex + rightIndices
}

internal fun calculateAlternatePopupLeftOptionCount(
    optionCount: Int,
    availableLeftWidth: Int,
    availableRightWidth: Int
): Int {
    require(optionCount > 0)
    require(availableLeftWidth >= 0)
    require(availableRightWidth >= 0)

    val availableWidth = availableLeftWidth + availableRightWidth
    if (availableWidth == 0) {
        return (optionCount - 1) / 2
    }

    return (((optionCount - 1) * availableLeftWidth.toFloat()) /
            availableWidth).roundToInt().coerceIn(0, optionCount - 1)
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
