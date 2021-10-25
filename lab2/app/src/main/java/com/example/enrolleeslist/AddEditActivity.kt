package com.example.enrolleeslist

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditActivity : AppCompatActivity() {
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var paternal: EditText
    private lateinit var city: EditText
    private lateinit var grades: EditText
    private var enrollee: Enrollee? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        firstName = findViewById(R.id.firstNameInput)
        lastName = findViewById(R.id.secondNameInput)
        paternal = findViewById(R.id.paternalInput)
        city = findViewById(R.id.cityInput)
        grades = findViewById(R.id.gradesInput)

        val arguments: Bundle? = intent.extras
        if (arguments != null) {
            enrollee = arguments.getParcelable("enrollee")!!

            if (enrollee != null) {
                firstName.setText(enrollee!!.firstName)
                lastName.setText(enrollee!!.secondName)
                if (enrollee!!.paternal != null) {
                    paternal.setText(enrollee!!.paternal)
                }
                city.setText(enrollee!!.city)
                var gradesStr = ""
                enrollee!!.grades.forEach {
                    gradesStr += "${it},"
                }
                grades.setText(gradesStr)
            }
        }
    }

    fun backOnClick(view: android.view.View) {
        toMainActivity()
    }

    private fun toMainActivity() {
        if (enrollee != null) {
            MainActivity.adapter.add(enrollee)
            MainActivity.adapter.notifyDataSetChanged()
        }
        finish()
    }

    fun submitOnClick(view: android.view.View) {
        try {
            val gradesStr = grades.text.split(',') as ArrayList
            gradesStr.removeIf { it == "" }
            val resultGrades = gradesStr.map { it.toInt() }

            enrollee = Enrollee(
                firstName.text.toString(),
                lastName.text.toString(),
                city.text.toString(),
                resultGrades as ArrayList<Int>,
                paternal.text.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.inputError), Toast.LENGTH_LONG).show()
            return
        }
        toMainActivity()
    }
}