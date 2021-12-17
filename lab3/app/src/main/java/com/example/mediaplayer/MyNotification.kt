package com.example.mediaplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyNotification {
    private val channelId1 = "channel_1"
    val actionPrevious = "actionPrevious"
    val actionPlay = "actionPlay"
    val actionNext = "actionNext"

    private lateinit var notificationManager: NotificationManager

    fun initManager(context: Context) {
        notificationManager = context.getSystemService(NotificationManager::class.java)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }

    fun createNotification(
        context: Context,
        isPlaying: Boolean,
        title: String? = "Unknown",
        artist: String? = "Unknown"
    ) {
        notificationManager = context.getSystemService(NotificationManager::class.java)
        val mediaSessionCompat = MediaSessionCompat(context, "tag")
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        val intentPrevious = Intent(context, NotificationActionReceiver::class.java)
            .setAction(actionPrevious)
        val pendingIntentPrevious = PendingIntent.getBroadcast(
            context, 0, intentPrevious,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val drwPrevious: Int = R.drawable.ic_baseline_skip_previous_24

        val intentPlay = Intent(context, NotificationActionReceiver::class.java)
            .setAction(actionPlay)
        val pendingIntentPlay = PendingIntent.getBroadcast(
            context, 0, intentPlay,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val drwPlay =
            when {
                isPlaying -> R.drawable.ic_baseline_pause_24
                else -> R.drawable.ic_baseline_play_arrow_24
            }

        val intentNext = Intent(context, NotificationActionReceiver::class.java)
            .setAction(actionNext)
        val pendingIntentNext = PendingIntent.getBroadcast(
            context, 0, intentNext,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val drwNext: Int = R.drawable.ic_baseline_skip_next_24

        val notificationChannel1 =
            NotificationChannel(channelId1, "channel 1", NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(notificationChannel1)

        val notification = NotificationCompat.Builder(context, channelId1)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setContentTitle(title)
            .setContentText(artist)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .addAction(drwPrevious, "Previous", pendingIntentPrevious)
            .addAction(drwPlay, "Play", pendingIntentPlay)
            .addAction(drwNext, "Next", pendingIntentNext)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .build()

        notificationManagerCompat.notify(1, notification)
    }
}