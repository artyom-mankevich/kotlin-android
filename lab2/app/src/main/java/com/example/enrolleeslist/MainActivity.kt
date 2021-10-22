package com.example.enrolleeslist

import android.database.DataSetObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var enrollees: ArrayList<Enrollee> = arrayListOf()
    private var origEnrollees: ArrayList<Enrollee> = arrayListOf()
    private lateinit var enrolleesListView: ListView
    private lateinit var editText: EditText
    private lateinit var adapter: EnrolleeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setInitialData()
        origEnrollees = enrollees.toMutableList() as ArrayList<Enrollee>

        enrolleesListView = findViewById(R.id.enrolleesList)
        val enrolleesCountTextView = findViewById<TextView>(R.id.enrolleesCount)
        enrolleesCountTextView.text = getCountWithAvgHigher(enrollees).toString()

        adapter = EnrolleeAdapter(this, R.layout.list_item, enrollees)
        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                enrolleesCountTextView.text = getCountWithAvgHigher(enrollees).toString()
            }
        })
        enrolleesListView.adapter = adapter

        editText = findViewById<EditText>(R.id.searchCityEditView)
        editText.setOnEditorActionListener { view, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    adapter.clear()
                    adapter.addAll(getFilteredByCityAndGrades(origEnrollees))
                    adapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        enrollees.add(Enrollee("Иван", "Иванов", "Гродно", arrayOf(3, 4, 5), "Иванович"))
    }

    private fun setInitialData() {
        enrollees.add(Enrollee("Иван", "Иванов", "Минск", arrayOf(4, 4, 5), "Иванович"))
        enrollees.add(Enrollee("Джон", "Смит", "Вашингтон", arrayOf(5, 4, 5)))
        enrollees.add(Enrollee("Андрей", "Смирнов", "Солигорск", arrayOf(4, 5, 5)))
        enrollees.add(Enrollee("Артём", "Манкевич", "Минск", arrayOf(5, 5, 5), "Олегович"))
        enrollees.add(Enrollee("Константин", "Калиновский", "Минск", arrayOf(5, 5, 5)))
    }

    private fun getCountWithAvgHigher(list: ArrayList<Enrollee>): Int {
        return list.count { enrollee -> enrollee.getAverageGrade() >= 4.5 }
    }

    private fun getFilteredByCityAndGrades(list: ArrayList<Enrollee>): ArrayList<Enrollee> {
        if (editText.text.isNotEmpty()) {
            var filteredList: ArrayList<Enrollee> = arrayListOf()
            filteredList = list.filterTo(filteredList)
            { enrollee ->
                enrollee.city == editText.text.toString() && enrollee.getAverageGrade() >= 4.5
            }
            filteredList.sortedBy { it.secondName }
            return filteredList
        } else return list
    }
}