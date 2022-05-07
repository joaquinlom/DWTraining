package com.dwtraining.lom.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotifyManager {
    private lateinit var notificationManager: NotificationManager

    fun sendNotification(context: Context, cls: Class<*>, textNotification: String?, titleNotification: String, drawable: Int, idNotification: Int) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val vibrate = longArrayOf(100, 100, 200, 300)
        val resultIntent = Intent(context, cls).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        val resultPendingIntent = PendingIntent.getActivity(context, REQUEST_CODE_NOTIFICATION, resultIntent, pendingIntentFlags)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(drawable)
                .setContentTitle(titleNotification)
                .setContentText(textNotification)
                .setVibrate(vibrate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(drawable)
                .setContentTitle(titleNotification)
                .setContentText(textNotification)
                .setVibrate(vibrate)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
        }
        // se manda el id para notificar cual es la notificación que se actualizará
        with(NotificationManagerCompat.from(context)) {
            notify( if(idNotification == 0) SIMPLE_NOTIFICATION_ID else idNotification, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val SIMPLE_NOTIFICATION_ID = 1
        const val REQUEST_CODE_NOTIFICATION = 0
        private const val CHANNEL_ID = "122"
        private const val CHANNEL_NAME = "DroidBountyHunter"
        private const val CHANNEL_DESCRIPTION = "Notifications for the app"
    }

}