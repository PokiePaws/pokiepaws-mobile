package com.pokiepaws.mobile.ui.notifications

import androidx.compose.runtime.Immutable
import com.pokiepaws.mobile.domain.model.AppNotification

@Immutable
data class NotificationItems(val items: List<AppNotification>)
