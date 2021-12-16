package com.example.mediaplayer

import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import java.util.*


class MusicFragment : Fragment() {
    private var musicFiles: ArrayList<MusicFile> = arrayListOf()
    private lateinit var musicFileAdapter: MusicFileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.list_fragment, container, false)
        val listView: ListView = view.findViewById(R.id.itemsList)
        musicFileAdapter = MusicFileAdapter(requireContext(), R.layout.music_list_item, musicFiles)
        listView.adapter = musicFileAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
            intent.putExtra("musicFile", musicFileAdapter.getItem(position))
            intent.putExtra("musicFilePosition", position)
            intent.putExtra("musicFiles", musicFiles)
            startActivity(intent)
        }

        loadMusic()
        return view
    }

    private fun loadMusic() {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration: Int =
                    (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))).toInt()
                val url =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                val mins = (duration / 1000) / 60
                val secs = (duration / 1000) % 60
                var durationStr = ""
                if (mins < 10) durationStr += "0"
                durationStr += "$mins:"
                if (secs < 10) durationStr += "0"
                durationStr += secs.toString()

                musicFiles.add(MusicFile(url, title, artist, durationStr, null))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        musicFileAdapter.notifyDataSetChanged()
    }
}