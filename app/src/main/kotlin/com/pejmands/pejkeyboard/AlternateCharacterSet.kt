package com.pejmands.pejkeyboard

internal data class AlternateCharacterSet(
    val characters: List<String>,
    val defaultIndex: Int
) {
    init {
        require(characters.isNotEmpty())
        require(defaultIndex in characters.indices)
    }
}
