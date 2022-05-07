package com.dwtraining.lom.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.dwtraining.lom.R
import com.dwtraining.lom.activities.HomeActivity
import com.dwtraining.lom.data.DatabaseBountyHunter
import com.dwtraining.lom.utils.NotifyManager
import java.util.*

class NotificationService: Service() {
    private lateinit var timer: Timer
    private val database by lazy { DatabaseBountyHunter(this) }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this,getString(R.string.notification_service_created_message),
            Toast.LENGTH_SHORT).show()
        instance = this
    }
    fun sendNotification() {
        var message = ""
        val added = database.obtainFugitivesNotNotified().size
        val deleted = database.obtainDeletionLogsNotNotified().size
        if (added > 0) {
            message += getString(R.string.notification_service_added_number, added)
            if (deleted > 0)
                message += ", " + getString(R.string.notification_service_deleted_number, deleted)
        } else if (deleted > 0) {
            message += getString(R.string.notification_service_deleted_number, deleted)
        }
        if (message.isNotEmpty()) {
            NotifyManager().also {
                val titleNotification = getString(R.string.notification_service_title_notification)
                it.sendNotification(this, HomeActivity::class.java, message,
                    titleNotification, R.mipmap.ic_launcher, 0)
            }
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, getString(R.string.notification_service_service_running, startId),
            Toast.LENGTH_LONG).show()
        val delay = 0L // Sin delay
        val period = 1000L * 60 // Un minuto
        timer = Timer().also {
            it.schedule(object : TimerTask() {
                override fun run() { sendNotification() }
            }, delay, period)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        Toast.makeText(this, getString(R.string.notification_service_service_stopped),
            Toast.LENGTH_LONG).show()
        instance = null
    }

    companion object {
        private var instance: NotificationService? = null
        fun isRunning(): Boolean = instance != null

    }

}