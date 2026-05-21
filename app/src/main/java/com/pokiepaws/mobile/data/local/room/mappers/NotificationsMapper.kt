package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import com.pokiepaws.mobile.domain.model.AppNotification

fun NotificationEntity.toDomain(): AppNotification =
    AppNotification(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        type = type,
    )

fun AppNotification.toEntity(): NotificationEntity =
    NotificationEntity(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        type = type,
    )
