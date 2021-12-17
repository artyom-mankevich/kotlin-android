package com.example.mediaplayer.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.example.mediaplayer.R
import com.example.mediaplayer.file_models.VideoFile
import java.io.File

class VideoPlayerActivity : Activity() {
    private var videoFile: VideoFile? = null

    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.videoView)
        mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)
        videoView.requestFocus()
        videoView.setOnCompletionListener {
            it.seekTo(0)
            it.start()
        }

        val arguments: Bundle? = intent.extras
        if (arguments != null) {
            videoFile = arguments.getParcelable("videoFile")

            if (videoFile != null) {
                videoView.setVideoURI(Uri.fromFile(File(videoFile!!.path)))
                videoView.start()
            }
        }
    }

    override fun onDestroy() {
        videoView.stopPlayback()
        videoView.releasePointerCapture()
        super.onDestroy()
    }
}