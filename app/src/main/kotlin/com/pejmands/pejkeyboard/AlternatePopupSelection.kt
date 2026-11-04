package com.pejmands.pejkeyboard

import kotlin.math.roundToInt

internal fun buildAlternatePopupOrder(
    characterCount: Int,
    defaultIndex: Int
): List<Int> {
    require(characterCount > 0)
    require(defaultIndex in 0 until characterCount)

    val preferredLeftCount = characterCount / 2
    val leftIndices = (defaultIndex - 1 downTo 0).toList().toMutableList()

    if (leftIndices.size < preferredLeftCount) {
        val additionalLeftIndices = (defaultIndex + 2 until characterCount).toList()
        leftIndices += additionalLeftIndices.take(preferredLeftCount - leftIndices.size)
    }

    val rightIndices = (defaultIndex + 1 until characterCount).toList() +
            (0 until defaultIndex).toList()

    return leftIndices.asReversed() + defaultIndex +
            rightIndices.take(characterCount - leftIndices.size - 1)
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
