package com.dwtraining.lom.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dwtraining.lom.services.NotificationService

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!NotificationService.isRunning()) {
            context?.startService(Intent(context, NotificationService::class.java))
        }
    }

}