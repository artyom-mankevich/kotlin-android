package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var inputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputField = findViewById(R.id.input_field)
        inputField.showSoftInputOnFocus = false
        inputField.requestFocus()
    }

    private fun insertInInputField(text: String) {
        val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd

        val start = inputField.selectionStart.coerceAtLeast(0)
        val end = inputField.selectionEnd.coerceAtLeast(0)
        val newText =
            inputField.text.replace(min(start, end), max(start, end), text, 0, text.length)
        inputField.text = newText
        inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
    }

    fun typeClick(view: View) {
        val button = view as Button
        insertInInputField(button.text.toString())
    }

    fun eraseClick(view: View) {
        val hasSelected = inputField.selectionEnd - inputField.selectionStart != 0
        if (hasSelected) {
            insertInInputField("")
        } else if (inputField.selectionEnd > 0) {
            val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
            val stringBuilder = StringBuilder(inputField.text.toString())
            stringBuilder.deleteAt(inputField.selectionEnd - 1)
            inputField.setText(stringBuilder)
            inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
        }
    }

    fun commaClick(view: View) {
        if (!inputField.text.contains(',')) {
            insertInInputField(",")
        }
    }

    fun clearAllButton(view: View) {
        inputField.setText("")
    }

    fun actionClick(view: View) {
        val buttonChar = (view as Button).text.single()
        val cursorPos = inputField.selectionEnd
        var prevChar: Char? = null
        val allIsSelected = cursorPos == inputField.text.length && inputField.selectionStart == 0

        if (allIsSelected && buttonChar != '-') {
            inputField.setText("")
            return
        }

        if (cursorPos > 0) {
            prevChar = inputField.text.toString()[cursorPos - 1]
        }
        when (prevChar) {
            '+', '-', '/', '*' -> {
                val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
                val stringBuilder = StringBuilder(inputField.text)
                stringBuilder.setCharAt(cursorPos - 1, buttonChar)
                inputField.setText(stringBuilder)
                inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
            }
            null -> {
            }
            else -> insertInInputField(buttonChar.toString())
        }
    }

    fun equalClick(view: View) {}
}