package com.example.mediaplayer.list_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.file_models.VideoFile

class VideoFileAdapter(context: Context, res: Int, videoFiles: ArrayList<VideoFile>) :
    ArrayAdapter<VideoFile>(context, res, videoFiles) {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var layout: Int = res
    private var list: ArrayList<VideoFile> = videoFiles

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = inflater.inflate(this.layout, parent, false)

        val titleView: TextView = view.findViewById(R.id.titleText)
        val durationView: TextView = view.findViewById(R.id.durationText)
        val videoFile: VideoFile = list[position]

        titleView.text = videoFile.title
        durationView.text = videoFile.duration

        return view
    }
}