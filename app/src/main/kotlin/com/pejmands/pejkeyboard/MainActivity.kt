package com.pejmands.pejkeyboard

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val message = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 20f
            setPadding(32, 32, 32, 32)
        }

        setContentView(message)
    }
}
