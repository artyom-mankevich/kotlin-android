package com.example.mediaplayer.file_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoFile(
    val path: String,
    val title: String,
    val duration: String
) : Parcelable {

}