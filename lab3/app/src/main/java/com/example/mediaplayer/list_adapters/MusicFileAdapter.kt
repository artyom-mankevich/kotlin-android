package com.example.mediaplayer.list_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.file_models.MusicFile

class MusicFileAdapter(context: Context, res: Int, musicFiles: ArrayList<MusicFile>) :
    ArrayAdapter<MusicFile>(context, res, musicFiles) {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var layout: Int = res
    private var list: ArrayList<MusicFile> = musicFiles

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = inflater.inflate(this.layout, parent, false)

        val titleView: TextView = view.findViewById(R.id.titleText)
        val artistView: TextView = view.findViewById(R.id.artistText)
        val durationView: TextView = view.findViewById(R.id.durationText)
        val artView: ImageView = view.findViewById(R.id.artImage)

        val musicFile: MusicFile = list[position]

        titleView.text = musicFile.title
        artistView.text = musicFile.artist
        durationView.text = musicFile.duration
        if (musicFile.art != null) {
            artView.setImageBitmap(musicFile.art)
        }

        return view
    }
}