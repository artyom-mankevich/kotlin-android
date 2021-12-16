package com.example.mediaplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs


class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var playButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var backButton: Button
    private lateinit var forthButton: Button

    private lateinit var seekBar: SeekBar

    private lateinit var artistText: TextView
    private lateinit var titleText: TextView
    private lateinit var lengthText: TextView
    private lateinit var timestampText: TextView

    private var musicFile: MusicFile? = null
    private var musicFilePosition: Int = 0
    private var musicFiles: ArrayList<MusicFile> = arrayListOf()

    private lateinit var detector: GestureDetectorCompat

    private var observer: MediaObserver? = null

    private inner class MediaObserver : Runnable {
        private val stop = AtomicBoolean(false)
        fun stop() {
            stop.set(true)
        }

        override fun run() {
            while (!stop.get() && mediaPlayer.isPlaying) {
                seekBar.progress =
                    (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble() * 100).toInt()
                val cur = mediaPlayer.currentPosition / 1000
                var str = ""
                val mins = cur / 60
                if (mins < 10) str += "0"
                str += "$mins:"
                val secs = cur % 60
                if (secs < 10) str += "0"
                str += secs
                runOnUiThread {
                    timestampText.text = str
                }
                Thread.sleep(500)
            }
        }
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (abs(velocityX) > 2 * abs(velocityY)) {
                // отрицательный - следующий
                // положительный - предыдущий
                if (velocityX < 0) {
                    if (musicFile != null) {
                        musicFilePosition = (musicFilePosition + 1) % musicFiles.count()
                        playCurrentTrack()
                    }
                } else previousTrack()
            }
            return true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            it.start()
            observer = MediaObserver()
            Thread(observer).start()
            playButton.background = ContextCompat.getDrawable(this, R.drawable.stop)
        }
        mediaPlayer.setOnCompletionListener {
            it.stop()
            it.reset()
            observer?.stop()
            observer = null
            musicFilePosition = (musicFilePosition + 1) % musicFiles.count()
            playCurrentTrack()
        }

        playButton = findViewById(R.id.playButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        forthButton = findViewById(R.id.forthButton)

        seekBar = findViewById(R.id.seekBar)

        artistText = findViewById(R.id.artistText)
        titleText = findViewById(R.id.titleText)
        lengthText = findViewById(R.id.lengthText)
        timestampText = findViewById(R.id.timestampText)


        val arguments: Bundle? = intent.extras
        if (arguments != null) {
            musicFile = arguments.getParcelable("musicFile")
            musicFilePosition = arguments.getInt("musicFilePosition")
            musicFiles = arguments.getParcelableArrayList("musicFiles")!!

            if (musicFile != null) {
                artistText.text = musicFile!!.artist
                titleText.text = musicFile!!.title
                lengthText.text = musicFile!!.duration
                openMediaFile(Uri.fromFile(File(musicFile!!.path)))
            }
        }

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val time = progress * mediaPlayer.duration / 100
                        mediaPlayer.seekTo(time)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        detector = GestureDetectorCompat(this, MyGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }


    private fun openMediaFile(fileUri: Uri) {
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

    fun playButtonOnClick(view: android.view.View) {
        if (musicFile == null) return

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playButton.background = ContextCompat.getDrawable(this, R.drawable.play)
        } else {
            mediaPlayer.start()
            playButton.background = ContextCompat.getDrawable(this, R.drawable.stop)
        }
    }

    override fun onStop() {
        super.onStop()
        observer?.stop()
        mediaPlayer.release()
    }

    private fun changeText() {
        artistText.text = musicFile!!.artist
        titleText.text = musicFile!!.title
        lengthText.text = musicFile!!.duration
        timestampText.text = "0:00"
    }

    fun nextButtonOnClick(view: android.view.View) {
        if (musicFile == null) return

        musicFilePosition = (musicFilePosition + 1) % musicFiles.count()
        playCurrentTrack()
    }

    private fun playCurrentTrack() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(musicFiles[musicFilePosition].path)
        mediaPlayer.prepareAsync()
        musicFile = musicFiles[musicFilePosition]
        changeText()
    }

    fun prevButtonOnClick(view: android.view.View) {
        if (musicFile == null) return
        previousTrack()
    }

    private fun previousTrack() {
        if (mediaPlayer.currentPosition in 0..5000 || mediaPlayer.duration < 5000) {
            musicFilePosition -= 1
            if (musicFilePosition < 0) musicFilePosition = musicFiles.count() - 1
            playCurrentTrack()
        } else {
            mediaPlayer.seekTo(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        observer?.stop()
        observer = null
        mediaPlayer.release()
    }

    fun backButtonOnClick(view: android.view.View) {
        val time = (mediaPlayer.currentPosition - 10000).coerceAtLeast(0)
        mediaPlayer.seekTo(time)
    }

    fun forthButtonOnClick(view: android.view.View) {
        val time = (mediaPlayer.currentPosition + 10000).coerceAtMost(mediaPlayer.duration)
        mediaPlayer.seekTo(time)
    }
}