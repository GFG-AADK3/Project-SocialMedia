package com.example.aadk3_social_app.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.isInputValid(parentLayout: TextInputLayout, errorMessage: String): Boolean {

    if (this.text.toString().isBlank()) {
        parentLayout.error = errorMessage
        return false
    }

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            parentLayout.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
    })

    return true
}
