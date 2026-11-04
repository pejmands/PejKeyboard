package com.pejmands.pejkeyboard

internal const val MAX_ALTERNATE_POPUP_OPTIONS = 8

internal data class AlternateCharacterSet(
    val characters: List<String>,
    val defaultIndex: Int
) {
    init {
        require(characters.isNotEmpty())
        require(defaultIndex in characters.indices)
    }

    fun limitedTo(maxCount: Int): AlternateCharacterSet {
        require(maxCount > 0)
        if (characters.size <= maxCount) {
            return this
        }

        val limitedCharacters = characters.take(maxCount).toMutableList()
        val limitedDefaultIndex = defaultIndex.coerceAtMost(maxCount - 1)
        if (defaultIndex >= maxCount) {
            limitedCharacters[limitedDefaultIndex] = characters[defaultIndex]
        }

        return AlternateCharacterSet(limitedCharacters, limitedDefaultIndex)
    }
}
