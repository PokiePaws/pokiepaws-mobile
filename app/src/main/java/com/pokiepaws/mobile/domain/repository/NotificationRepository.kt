package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val notifications: Flow<List<AppNotification>>

    suspend fun markAsRead(notificationId: Int)

    suspend fun markAllAsRead()
}
