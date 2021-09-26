package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.notkamui.keval.Keval
import com.notkamui.keval.KevalInvalidExpressionException
import com.notkamui.keval.KevalInvalidSymbolException
import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var inputField: EditText
    private lateinit var resultField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputField = findViewById(R.id.input_field)
        inputField.showSoftInputOnFocus = false
        inputField.requestFocus()

        resultField = findViewById(R.id.result_field)
        resultField.movementMethod = ScrollingMovementMethod()
        resultField.setTextIsSelectable(true)
    }

    fun typeClick(view: View) {
        val button = view as Button
        insertInInputField(button.text.toString())
    }

    fun eraseClick(view: View) {
        if (inputField.selectionEnd - inputField.selectionStart != 0) {
            insertInInputField("")
        } else if (inputField.selectionEnd > 0) {
            val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
            val stringBuilder = StringBuilder(inputField.text.toString())
            stringBuilder.deleteAt(inputField.selectionEnd - 1)
            inputField.setText(stringBuilder)
            inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
        }
    }

    fun dotClick(view: View) {
        val buttonChar = (view as Button).text.single()
        val cursorPos = inputField.selectionEnd.coerceAtLeast(0)
        val hasSelected = cursorPos - inputField.selectionStart.coerceAtLeast(0) != 0
        val text = inputField.text

        // if previous character is comma -> return
        if (cursorPos > 0) {
            val prevChar = text[cursorPos - 1]
            if (prevChar == buttonChar) {
                return
            }
        }

        // if user selected something or text is empty -> replace the selected with comma
        if (hasSelected || text.isEmpty()) {
            insertInInputField(buttonChar.toString())
            return
        }

        val number = getEnclosedNumber(text, cursorPos)
        if (number.contains(buttonChar)) {
            return
        } else {
            insertInInputField(buttonChar.toString())
        }
    }

    fun clearAllButton(view: View) {
        inputField.setText("")
        resultField.text = ""
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
            prevChar = inputField.text[cursorPos - 1]
        }

        when (prevChar) {
            '+', '-', '/', '*' -> {
                val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
                val stringBuilder = StringBuilder(inputField.text)
                stringBuilder.setCharAt(cursorPos - 1, buttonChar)
                inputField.setText(stringBuilder)
                inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
            }
            null -> return
            else -> insertInInputField(buttonChar.toString())
        }
    }

    fun equalClick(view: View) {
        val text = inputField.text.toString()

        if (text.isEmpty()) {
            return
        }

        try {
            val result = Keval.eval(text)
            resultField.text = result.toString()
        } catch (e: KevalZeroDivisionException) {
            resultField.text = getString(R.string.zeroDivision)
        } catch (e: KevalInvalidSymbolException) {
            resultField.text = getString(R.string.invalidOperator)
        } catch (e: KevalInvalidExpressionException) {
            resultField.text = getString(R.string.invalidExpression)
        }

    }

    private fun getEnclosedNumber(text: CharSequence, cursorPos: Int): CharSequence {
        // index of first digit of number
        val numberStart = getNumberStart(text, cursorPos)

        // index of last digit of number
        var numberEnd = getNumberEnd(text, cursorPos)

        if (numberEnd == -1) {
            // if no arithmetic signs were found right of cursor
            numberEnd = text.length
        } else if (numberEnd != text.length) {
            // if arithmetic sign was found,
            // numberEnd is assigned the index of that sign as if it was in original array
            numberEnd += cursorPos + 1
        }

        return text.slice(numberStart until numberEnd)
    }

    private fun getNumberStart(text: CharSequence, cursorPos: Int): Int {
        return text.slice(0 until cursorPos).indexOfLast {
            it == '-' || it == '+' || it == '/' || it == '*'
        }.coerceAtLeast(0)
    }

    private fun getNumberEnd(text: CharSequence, cursorPos: Int): Int {
        val numberEnd = if (cursorPos != text.length) {
            text.slice(cursorPos until text.length).indexOfFirst {
                it == '-' || it == '+' || it == '/' || it == '*'
            }
        } else {
            text.length
        }
        return numberEnd
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
}