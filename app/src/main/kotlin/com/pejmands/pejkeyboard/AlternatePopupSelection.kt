package com.pejmands.pejkeyboard

internal fun findAlternateOptionIndex(
    x: Float,
    optionBounds: List<Pair<Int, Int>>
): Int {
    val position = x.toInt()
    return optionBounds.indexOfFirst { (left, rightExclusive) ->
        position >= left && position < rightExclusive
    }
}
