package com.example.mediaplayer.fragments

import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.mediaplayer.R
import com.example.mediaplayer.activities.VideoPlayerActivity
import com.example.mediaplayer.file_models.VideoFile
import com.example.mediaplayer.list_adapters.VideoFileAdapter
import java.util.*

class VideosFragment : AbstractFragment() {
    private var videoFiles: ArrayList<VideoFile> = arrayListOf()
    private lateinit var videoFileAdapter: VideoFileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.list_fragment, container, false)
        val listView: ListView = view.findViewById(R.id.itemsList)
        videoFileAdapter = VideoFileAdapter(requireContext(), R.layout.video_list_item, videoFiles)
        listView.adapter = videoFileAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
            intent.putExtra("videoFile", videoFileAdapter.getItem(position))
            startActivity(intent)
        }

        loadVideos()
        return view
    }

    private fun loadVideos() {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                val duration: Int =
                    (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))).toInt()
                val url =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))

                val durationStr = super.getFormattedDurationString(duration)

                videoFiles.add(VideoFile(url, title, durationStr))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        videoFileAdapter.notifyDataSetChanged()
    }
}
