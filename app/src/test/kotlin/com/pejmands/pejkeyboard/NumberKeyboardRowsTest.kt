package com.pejmands.pejkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberKeyboardRowsTest {

    @Test
    fun keepsTheNumericAndFirstSymbolRowsUnchanged() {
        assertEquals(
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰"),
            NumberKeyboardRows.rows[0]
        )
        assertEquals(
            listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")", "/"),
            NumberKeyboardRows.rows[1]
        )
    }

    @Test
    fun placesMathOperatorsQuotesAndPunctuationOnTheThirdRow() {
        assertEquals(
            listOf("*", "×", "÷", "=", "^", "_", "«", "»", "؟", "!"),
            NumberKeyboardRows.rows[2]
        )
    }

    @Test
    fun keepsEveryNumericKeyboardRowAtTenKeys() {
        assertEquals(3, NumberKeyboardRows.rows.size)
        assertEquals(listOf(10, 10, 10), NumberKeyboardRows.rows.map { it.size })
    }
}
