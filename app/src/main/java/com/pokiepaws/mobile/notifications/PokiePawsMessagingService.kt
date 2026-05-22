package com.pokiepaws.mobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pokiepaws.mobile.MainActivity
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PokiePawsMessagingService : FirebaseMessagingService() {
    private companion object {
        const val FCM_LOG_TAG = "FCM_MESSAGE"
        const val DATABASE_LOG_TAG = "FCM_DATABASE"
    }

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var deviceTokenRegistrar: DeviceTokenRegistrar

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(FCM_LOG_TAG, "Push notification received (Data payload keys: ${remoteMessage.data.keys})")

        val title = remoteMessage.data["title"] ?: "PokiePaws"
        val message = remoteMessage.data["body"] ?: "Brak treści"
        val type = remoteMessage.data["type"]

        showNotification(title, message)
        saveToDatabase(title, message, type)
    }

    private fun saveToDatabase(
        title: String,
        message: String,
        type: String?,
    ) {
        serviceScope.launch {
            try {
                val entity =
                    NotificationEntity(
                        title = title,
                        content = message,
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                        type = type,
                    )
                notificationDao.insertNotification(entity)
                Log.d(DATABASE_LOG_TAG, "Notification saved locally in Room")
            } catch (e: android.database.sqlite.SQLiteException) {
                Log.e(DATABASE_LOG_TAG, "SQLite error while saving notification", e)
            } catch (e: IllegalStateException) {
                Log.e(DATABASE_LOG_TAG, "Notification database unavailable", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(FCM_LOG_TAG, "New device FCM registration value received")
        deviceTokenRegistrar.registerToken(token)
    }

    private fun showNotification(
        title: String,
        message: String,
    ) {
        val channelId = "appointment_reminders"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(
                channelId,
                "Przypomnienia o wizytach",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Powiadomienia o zbliżających się wizytach u weterynarza"
            }
        notificationManager.createNotificationChannel(channel)

        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
