package com.example.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs


class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var mService: MusicPlayerService
    private var mBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.MyBinder
            mService = binder.getService()
            mService.setCallBack(this@MusicPlayerActivity)
            mBound = true
            observer = MediaObserver()
            Thread(observer).start()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

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
            while (!stop.get()) {
                val curPos = mService.getCurrentPosition()
                seekBar.progress =
                    (curPos.toDouble() / mService.getDuration().toDouble() * 100).toInt()
                val cur = curPos / 1000
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
                if (musicFile != null) {
                    if (velocityX < 0) {
                        mService.playNextTrack()
                        musicFile = mService.getCurrentFile()
                        changeText()
                    } else {
                        mService.playPreviousTrack()
                        musicFile = mService.getCurrentFile()
                        changeText()
                    }
                }
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

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
                        val time = progress * mService.getDuration() / 100
                        mService.seekTo(time)
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

    override fun onStart() {
        super.onStart()
        Intent(this, MusicPlayerService::class.java).also { intent ->
            intent.putExtra("musicFile", musicFile)
            intent.putExtra("musicFiles", musicFiles)
            intent.putExtra("musicFilePosition", musicFilePosition)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun playButtonOnClick(view: android.view.View) {
        if (musicFile == null) return

        if (mBound) {
            if (mService.getIsPlaying()) {
                playButton.background = ContextCompat.getDrawable(this, R.drawable.play)
            } else {
                playButton.background = ContextCompat.getDrawable(this, R.drawable.stop)
            }
            mService.playPause()
        }
    }

    override fun onStop() {
        super.onStop()
        observer?.stop()
        if (mBound) {
            musicFile = mService.getCurrentFile()
            changeText()
            unbindService(connection)
            mBound = false
        }
    }

    override fun onPause() {
        super.onPause()
        unbindService(connection)
        mBound = false
    }

    fun changeText() {
        artistText.text = musicFile!!.artist
        titleText.text = musicFile!!.title
        lengthText.text = musicFile!!.duration
        timestampText.text = "0:00"
        playButton.background = ContextCompat.getDrawable(this, R.drawable.stop)
    }

    fun setMusicFile(file: MusicFile) {
        musicFile = file
    }

    fun nextButtonOnClick(view: android.view.View) {
        if (musicFile == null) return

        if (mBound) {
            mService.playNextTrack()
            musicFile = mService.getCurrentFile()
            changeText()
        }
    }

    fun prevButtonOnClick(view: android.view.View) {
        if (musicFile == null) return
        if (mBound) {
            mService.playPreviousTrack()
            musicFile = mService.getCurrentFile()
            changeText()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        observer?.stop()
        observer = null
    }

    fun backButtonOnClick(view: android.view.View) {
        if (mBound) {
            mService.backTenSecs()
        }
    }

    fun forthButtonOnClick(view: android.view.View) {
        if (mBound) {
            mService.forthTenSecs()
        }
    }
}