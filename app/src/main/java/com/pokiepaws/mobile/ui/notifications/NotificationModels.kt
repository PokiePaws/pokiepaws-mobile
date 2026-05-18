package com.pokiepaws.mobile.ui.notifications

import com.pokiepaws.mobile.domain.model.AppNotification

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
)
