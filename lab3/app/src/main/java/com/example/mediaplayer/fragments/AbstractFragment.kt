package com.example.mediaplayer.fragments

import androidx.fragment.app.Fragment

abstract class AbstractFragment : Fragment() {
    fun getFormattedDurationString(duration: Int): String {
        var durationStr = ""
        val mins = (duration / 1000) / 60
        val secs = (duration / 1000) % 60
        if (mins < 10) durationStr += "0"
        durationStr += "$mins:"
        if (secs < 10) durationStr += "0"
        durationStr += secs.toString()
        return durationStr
    }
}