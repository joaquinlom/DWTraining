package com.dwtraining.lom.services

import android.util.Log
import com.dwtraining.lom.R
import com.dwtraining.lom.activities.HomeActivity
import com.dwtraining.lom.utils.NotifyManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TokenFirebaseService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, getString(R.string.token_firebase_service_new_token, token))
    }

    private companion object {
        val TAG = TokenFirebaseService::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"FireBase Services creado")
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationFrom = remoteMessage.from ?: ""
        val body: String
        Log.d(TAG, getString(R.string.token_firebase_service_new_token, notificationFrom))
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, getString(R.string.token_firebase_service_message_payload, remoteMessage.data))
        }
        if (remoteMessage.notification != null) {
            body = remoteMessage.notification?.body ?: ""
            Log.d(TAG, getString(R.string.token_firebase_service_message_body, body))
            NotifyManager().sendNotification(this, HomeActivity::class.java, body,
                notificationFrom, R.mipmap.ic_launcher, 1)
        }
    }

}