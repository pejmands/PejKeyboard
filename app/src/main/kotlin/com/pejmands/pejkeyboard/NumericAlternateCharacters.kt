package com.pejmands.pejkeyboard

internal object NumericAlternateCharacters {
    val byKey = mapOf(
        "۱" to AlternateCharacterSet(listOf("1", "½", "⅓", "¼", "⅕", "⅙", "⅐", "¹"), 0),
        "۲" to AlternateCharacterSet(listOf("2", "⅔", "⅖", "²"), 0),
        "۳" to AlternateCharacterSet(listOf("3", "¾", "⅗", "⅜", "³"), 0),
        "۴" to AlternateCharacterSet(listOf("4", "⅘", "⁴"), 0),
        "۵" to AlternateCharacterSet(listOf("5", "⅚", "⅝", "⁵"), 0),
        "۶" to AlternateCharacterSet(listOf("6", "⁶"), 0),
        "۷" to AlternateCharacterSet(listOf("7", "⅞", "⁷"), 0),
        "۸" to AlternateCharacterSet(listOf("8", "⁸", "∞"), 0),
        "۹" to AlternateCharacterSet(listOf("9", "⁹", "⁽"), 0),
        "۰" to AlternateCharacterSet(listOf("0", "⁰", "⁾", "°"), 0)
    )
}
