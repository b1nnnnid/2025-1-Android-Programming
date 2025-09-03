package com.example.momapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.jvm.java

class MyNotificationHelper(private val context: Context) {
    fun showNotification(title: String?, message: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        // Android 8.0 이상 Notification Channel 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "one-channel"
            val channelName = "MomCare+"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(context, channelId)
        }
        else {
            builder = NotificationCompat.Builder(context)
        }
        //알림 빌딩
        builder.run {
            setSmallIcon(R.drawable.ic_stat_ic_notification)
            setWhen(System.currentTimeMillis())
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)
        }
        //알림 보내기...11은 아이디
        notificationManager.notify(11, builder.build())
    }
}