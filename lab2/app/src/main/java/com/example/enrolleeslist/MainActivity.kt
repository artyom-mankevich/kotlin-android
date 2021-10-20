package com.example.enrolleeslist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    private var enrollees: ArrayList<Enrollee> = arrayListOf()
    private lateinit var enrolleesListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInitialData()
        enrolleesListView = findViewById(R.id.enrolleesList)
        val adapter = EnrolleeAdapter(this, R.layout.list_item, enrollees)
        enrolleesListView.adapter = adapter
    }

    private fun setInitialData() {
        enrollees.add(Enrollee("Иван", "Иванов", "Минск", arrayOf(4, 4, 5), "Иванович"))
        enrollees.add(Enrollee("Джон", "Смит", "Вашингтон", arrayOf(5, 4, 5)))
        enrollees.add(Enrollee("Андрей", "Смирнов", "Солигорск", arrayOf(4, 5, 5)))
        enrollees.add(Enrollee("Артём", "Манкевич", "Минск", arrayOf(5, 5, 5), "Олегович"))
        enrollees.add(Enrollee("Константин", "Калиновский", "Минск", arrayOf(5, 5, 5)))
    }

    private fun getCountWithAvgHigher(): Int {
        return enrollees.count { enrollee -> enrollee.getAverageGrade() >= 4.5 }
    }
}