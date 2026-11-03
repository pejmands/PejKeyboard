package com.pejmands.pejkeyboard

internal object BackspaceWordDeletion {

    fun length(textBeforeCursor: CharSequence): Int {
        if (textBeforeCursor.isEmpty()) {
            return 0
        }

        var wordEnd = textBeforeCursor.length
        while (wordEnd > 0 && textBeforeCursor[wordEnd - 1].isWhitespace()) {
            wordEnd--
        }

        if (wordEnd == 0) {
            return textBeforeCursor.length
        }

        var wordStart = wordEnd
        while (wordStart > 0 && !textBeforeCursor[wordStart - 1].isWhitespace()) {
            wordStart--
        }

        while (wordStart > 0 && textBeforeCursor[wordStart - 1].isWhitespace()) {
            wordStart--
        }

        return textBeforeCursor.length - wordStart
    }
}
