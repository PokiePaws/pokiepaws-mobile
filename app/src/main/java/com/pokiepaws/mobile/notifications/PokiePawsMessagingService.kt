package com.pokiepaws.mobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
    @Inject
    lateinit var notificationDao: NotificationDao
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "PokiePaws"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: "Nowa wiadomość"

        Log.d("FCM_TEST", "Wiadomość przyszła! Title: $title")

        showNotification(title, message)

        saveToDatabase(title, message)
    }

    private fun saveToDatabase(
        title: String,
        message: String,
    ) {
        serviceScope.launch {
            try {
                val entity =
                    NotificationEntity(
                        title = title,
                        content = message,
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                    )
                notificationDao.insertNotification(entity)
                Log.d("FCM_DATABASE", "Powiadomienie zapisane lokalnie w Room")
            } catch (e: android.database.sqlite.SQLiteException) {
                Log.e("FCM_DATABASE", "Błąd SQLite przy zapisie powiadomienia", e)
            } catch (e: IllegalStateException) {
                Log.e("FCM_DATABASE", "Baza danych niedostępna", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nowy token urządzenia: $token")
    }

    private fun showNotification(
        title: String?,
        message: String?,
    ) {
        val channelId = "appointment_reminders"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Przypomnienia o wizytach",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Powiadomienia o zbliżających się wizytach u weterynarza"
                }
            notificationManager.createNotificationChannel(channel)
        }

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
