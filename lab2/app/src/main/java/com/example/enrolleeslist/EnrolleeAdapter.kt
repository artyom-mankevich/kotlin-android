package com.example.enrolleeslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class EnrolleeAdapter(context: Context, res: Int, enrollees: ArrayList<Enrollee>) :
    ArrayAdapter<Enrollee>(
        context,
        res,
        enrollees
    ) {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var layout: Int = res
    private var list: ArrayList<Enrollee> = enrollees

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = inflater.inflate(this.layout, parent, false)

        val firstNameView: TextView = view.findViewById(R.id.enrolleeFirstName)
        val secondNameView: TextView = view.findViewById(R.id.enrolleeSecondName)
        val paternalView: TextView = view.findViewById(R.id.enrolleePaternal)
        val cityView: TextView = view.findViewById(R.id.enrolleeCity)
        val avgGradeView: TextView = view.findViewById(R.id.enrolleeAvgGrade)

        val enrollee: Enrollee = list[position]

        firstNameView.text = enrollee.firstName
        secondNameView.text = enrollee.secondName
        paternalView.text = enrollee.paternal
        cityView.text = enrollee.city
        fun Double.format(digits: Int) = "%.${digits}f".format(this)
        avgGradeView.text = enrollee.getAverageGrade().format(2)

        return view
    }
}