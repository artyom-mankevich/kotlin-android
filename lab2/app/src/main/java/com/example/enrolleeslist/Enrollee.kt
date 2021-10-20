package com.example.enrolleeslist

class Enrollee(
    val firstName: String,
    val secondName: String,
    _city: String,
    private val grades: Array<Int>,
    val paternal: String? = null
) {
    var city: String = _city

    fun getAverageGrade(): Double {
        return grades.average()
    }
}