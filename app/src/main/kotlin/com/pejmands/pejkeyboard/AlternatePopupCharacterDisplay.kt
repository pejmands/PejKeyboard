package com.pejmands.pejkeyboard

private val diacriticDisplayCharacters = mapOf(
    "\u0651" to "\uFE7C",
    "\u064E" to "\uFE76",
    "\u0650" to "\uFE7A",
    "\uُ" to "\uFE78",
    "\u064B" to "\uFE70",
    "\u0652" to "\uFE7E"
)

internal fun alternatePopupCharacterDisplay(character: String): String {
    return diacriticDisplayCharacters[character] ?: character
}

internal fun isDiacriticAlternateCharacter(character: String): Boolean {
    return character in setOf(
        "\u0651",
        "\u064E",
        "\u0650",
        "\uُ",
        "\u064B",
        "\u0652",
        "\u0670",
        "\u0654"
    )
}
