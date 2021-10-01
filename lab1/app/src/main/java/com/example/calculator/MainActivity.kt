package com.example.calculator

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.notkamui.keval.Keval
import com.notkamui.keval.KevalInvalidExpressionException
import com.notkamui.keval.KevalInvalidSymbolException
import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    private lateinit var inputField: EditText
    private var advancedIsDisplayed = false
    private lateinit var advancedFragment: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputField = findViewById(R.id.inputField)
        inputField.showSoftInputOnFocus = false // to disable default keyboard popping up
        inputField.requestFocus()

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // advanced fragment is always displayed in landscape mode
            advancedFragment = findViewById(R.id.advancedFragment)
            advancedFragment.visibility = View.GONE
        }
    }

    fun typeClick(view: View) {
        // all buttons that require just displaying typed character like number and brackets buttons
        val button = view as Button
        insertInInputField(button.text.toString())
    }

    fun eraseClick(view: View) {
        if (inputField.selectionEnd - inputField.selectionStart != 0) {
            // if something is selected -> erase selected area
            insertInInputField("")
        } else if (inputField.selectionEnd > 0) {
            // cursor position is stored from the end so it is saved after changing the text
            val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
            val stringBuilder = StringBuilder(inputField.text.toString())
            stringBuilder.deleteAt(inputField.selectionEnd - 1)
            inputField.setText(stringBuilder)
            inputField.setSelection(inputField.text.length - cursorPositionFromEnd) // setting the cursor
        }
    }

    fun dotClick(view: View) {
        val buttonChar = (view as Button).text.single()
        val cursorPos = inputField.selectionEnd.coerceAtLeast(0)
        val hasSelected = cursorPos - inputField.selectionStart.coerceAtLeast(0) != 0
        val text = inputField.text

        // if previous character is dot -> return
        if (cursorPos > 0) {
            val prevChar = text[cursorPos - 1]
            if (prevChar == buttonChar) {
                return
            }
        }

        // if user selected something or text is empty -> replace the selected with dot
        if (hasSelected || text.isEmpty()) {
            insertInInputField(buttonChar.toString())
            return
        }

        // number is something enclosed by operators
        val number = getEnclosedNumber(text, cursorPos)
        if (number.contains(buttonChar)) {
            return
        } else {
            insertInInputField(buttonChar.toString())
        }
    }

    fun clearAllButton(view: View) {
        inputField.setText("")
    }

    // action click is for arithmetic operators
    fun actionClick(view: View) {
        val buttonChar = (view as Button).text.single()
        val cursorPos = inputField.selectionEnd
        var prevChar: Char? = null
        val allIsSelected = cursorPos == inputField.text.length && inputField.selectionStart == 0

        if (allIsSelected) {
            if (buttonChar != '-') {
                inputField.setText("")
                return
            } else if (buttonChar == '-') {
                val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
                inputField.setText("-")
                inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
                return
            }
        }

        if (cursorPos > 0) {
            prevChar = inputField.text[cursorPos - 1]
        }

        when (prevChar) {
            '+', '-' -> {
                replacePrevChar(buttonChar)
            }
            '/', '*', '^', '%' -> {
                if (buttonChar == '-') {
                    insertInInputField(buttonChar.toString())
                } else {
                    replacePrevChar(buttonChar)
                }
            }
            null -> return
            else -> insertInInputField(buttonChar.toString())
        }
    }

    // equal click evaluates input expression
    fun equalClick(view: View) {
        val text = inputField.text.toString()

        if (text.isEmpty()) {
            return
        }

        try {
            // Keval is third-party library to evaluate string to arithmetic expression
            val result = Keval {
                includeDefault()

                // since Keval doesn't support sin and cos by default, add them manually
                function {
                    name = "sin"
                    arity = 1
                    implementation = { args -> sin(args[0]) }
                }

                function {
                    name = "cos"
                    arity = 1
                    implementation = { args -> cos(args[0]) }
                }
            }.eval(text)

            inputField.setText(result.toString())
            inputField.setSelection(0)
        } catch (e: KevalZeroDivisionException) {
            Toast.makeText(
                this, getString(R.string.zeroDivision),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: KevalInvalidSymbolException) {
            Toast.makeText(
                this, getString(R.string.invalidOperator),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: KevalInvalidExpressionException) {
            Toast.makeText(
                this, getString(R.string.invalidExpression),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun trigonometryClick(view: android.view.View) {
        val buttonText = (view as Button).text.toString() + "("
        insertInInputField(buttonText)
    }

    // toggling advanced panel's visibility
    fun advancedCalcClick(view: android.view.View) {
        if (advancedIsDisplayed) {
            advancedFragment.visibility = View.GONE
            advancedIsDisplayed = false
        } else {
            advancedFragment.visibility = View.VISIBLE
            advancedIsDisplayed = true
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
            it == '-' || it == '+' || it == '/' || it == '*' || it == '^' || it == '%'
        }.coerceAtLeast(0)
    }

    private fun getNumberEnd(text: CharSequence, cursorPos: Int): Int {
        val numberEnd = if (cursorPos != text.length) {
            text.slice(cursorPos until text.length).indexOfFirst {
                it == '-' || it == '+' || it == '/' || it == '*' || it == '^' || it == '%'
            }
        } else {
            text.length
        }
        return numberEnd
    }

    // function that types text at current cursor position
    private fun insertInInputField(text: String) {
        val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd

        val start = inputField.selectionStart.coerceAtLeast(0)
        val end = inputField.selectionEnd.coerceAtLeast(0)
        val newText =
            inputField.text.replace(min(start, end), max(start, end), text, 0, text.length)
        inputField.text = newText
        inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
    }

    private fun replacePrevChar(char: Char) {
        val cursorPositionFromEnd = inputField.text.length - inputField.selectionEnd
        val stringBuilder = StringBuilder(inputField.text)
        stringBuilder.setCharAt(inputField.selectionEnd - 1, char)
        inputField.setText(stringBuilder)
        inputField.setSelection(inputField.text.length - cursorPositionFromEnd)
    }
}