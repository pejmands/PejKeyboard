package com.pejmands.pejkeyboard

private val diacriticDisplayCharacters = mapOf(
    "\u0651" to "\uFE7C",
    "\u064E" to "\uFE76",
    "\u0650" to "\uFE7A",
    "\u064F" to "\uFE78",
    "\u064B" to "\uFE70",
    "\u0652" to "\uFE7E",
    "\u0654" to "\uFE80"
)

private val diacriticCharacters = diacriticDisplayCharacters.keys + setOf(
    "\u0670",
    "\u0654"
)

internal fun alternatePopupCharacterDisplay(character: String): String {
    return diacriticDisplayCharacters[character] ?: character
}

internal fun isDiacriticAlternateCharacter(character: String): Boolean {
    return character in diacriticCharacters
}
