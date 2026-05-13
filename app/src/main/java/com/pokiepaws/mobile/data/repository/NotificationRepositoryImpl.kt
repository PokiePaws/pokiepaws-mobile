package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import com.pokiepaws.mobile.domain.model.AppNotification
import com.pokiepaws.mobile.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl
    @Inject
    constructor(
        private val notificationDao: NotificationDao,
    ) : NotificationRepository {
        override val notifications: Flow<List<AppNotification>> =
            notificationDao.getAllNotifications().map { notifications ->
                notifications.map { notification -> notification.toDomain() }
            }

        override suspend fun markAsRead(notificationId: Int) {
            notificationDao.markAsRead(notificationId)
        }

        override suspend fun markAllAsRead() {
            notificationDao.markAllAsRead()
        }
    }

private fun NotificationEntity.toDomain(): AppNotification =
    AppNotification(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        type = type,
    )
