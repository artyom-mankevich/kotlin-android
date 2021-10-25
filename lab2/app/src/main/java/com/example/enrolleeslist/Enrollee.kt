package com.example.enrolleeslist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
class Enrollee(
    var firstName: String,
    val secondName: String,
    val city: String,
    val grades: ArrayList<Int>,
    val paternal: String? = null
) : Parcelable {
    fun getAverageGrade(): Double {
        return grades.average()
    }
}