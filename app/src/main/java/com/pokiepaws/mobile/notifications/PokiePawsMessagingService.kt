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
        Log.d(
            FCM_LOG_TAG,
            "Push notification received (Data payload keys: ${remoteMessage.data.keys}, " +
                "has notification payload: ${remoteMessage.notification != null})",
        )

        val payload = remoteMessage.toAppNotificationPayload()

        showNotification(payload.title, payload.message)
        saveToDatabase(payload.title, payload.message, payload.type)
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
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun RemoteMessage.toAppNotificationPayload(): AppNotificationPayload {
        val data = data
        val notification = notification
        val title =
            data.firstNotBlank("title", "notificationTitle", "notification_title", "subject")
                ?: notification?.title?.takeIf { it.isNotBlank() }
                ?: "PokiePaws"
        val message =
            data.firstNotBlank("body", "message", "content", "text", "notificationBody", "notification_body")
                ?: notification?.body?.takeIf { it.isNotBlank() }
                ?: "Brak treści"

        return AppNotificationPayload(
            title = title,
            message = message,
            type = data.firstNotBlank("type", "notificationType", "notification_type"),
        )
    }

    private fun Map<String, String>.firstNotBlank(vararg keys: String): String? =
        keys.firstNotNullOfOrNull { key -> this[key]?.takeIf { it.isNotBlank() } }

    private data class AppNotificationPayload(
        val title: String,
        val message: String,
        val type: String?,
    )
}
