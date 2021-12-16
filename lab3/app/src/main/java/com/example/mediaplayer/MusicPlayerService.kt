package com.example.mediaplayer

import android.app.Service
import android.content.Intent
import android.content.Intent.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import java.io.File

class MusicPlayerService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = MyBinder()

    private var musicFile: MusicFile? = null
    private var musicFilePosition: Int = 0
    private var musicFiles: ArrayList<MusicFile> = arrayListOf()
    private var musicPlayer: MusicPlayerActivity? = null

    inner class MyBinder : Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    fun setCallBack(player: MusicPlayerActivity) {
        musicPlayer = player
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.setOnCompletionListener {
            it.stop()
            it.reset()
            playNextTrack()
            musicPlayer?.setMusicFile(musicFile!!)
            musicPlayer?.changeText()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val arguments: Bundle? = intent?.extras
        if (arguments != null) {
            musicFilePosition = arguments.getInt("musicFilePosition")
            musicFiles = arguments.getParcelableArrayList("musicFiles")!!

            val musicFileExtra = arguments.getParcelable<MusicFile>("musicFile")
            val isSameFile: Boolean = musicFileExtra?.path == musicFile?.path
            if (!isSameFile) {
                musicFile = musicFileExtra
            }

            if (musicFile != null && !isSameFile) {
                openMediaFile(Uri.fromFile(File(musicFile!!.path)))
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun openMediaFile(fileUri: Uri) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()

        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, fileUri)
            prepareAsync()
        }
    }

    private fun playCurrentTrack() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(musicFiles[musicFilePosition].path)
        mediaPlayer.prepareAsync()
        musicFile = musicFiles[musicFilePosition]
    }

    fun playPreviousTrack() {
        if (musicFiles.isEmpty()) return

        if (mediaPlayer.currentPosition in 0..5000 || mediaPlayer.duration < 5000) {
            musicFilePosition -= 1
            if (musicFilePosition < 0) musicFilePosition = musicFiles.count() - 1
            playCurrentTrack()
        } else {
            mediaPlayer.seekTo(0)
        }
    }

    fun playNextTrack() {
        if (musicFiles.isEmpty()) return

        musicFilePosition = (musicFilePosition + 1) % musicFiles.count()
        playCurrentTrack()
    }

    fun forthTenSecs() {
        if (musicFile == null) return

        val time = (mediaPlayer.currentPosition + 10000).coerceAtMost(mediaPlayer.duration)
        mediaPlayer.seekTo(time)
    }

    fun backTenSecs() {
        if (musicFile == null) return

        val time = (mediaPlayer.currentPosition - 10000).coerceAtLeast(0)
        mediaPlayer.seekTo(time)
    }

    fun playPause() {
        if (musicFile == null) return

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getIsPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrentFile(): MusicFile? {
        return musicFile
    }

    fun seekTo(time: Int) {
        if (musicFile != null)
            mediaPlayer.seekTo(time)
    }
}