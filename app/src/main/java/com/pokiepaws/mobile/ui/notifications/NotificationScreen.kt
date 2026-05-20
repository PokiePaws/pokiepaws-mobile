package com.pokiepaws.mobile.ui.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    NotificationContent(
        notifications = NotificationItems(notifications),
        onBack = onBack,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onNotificationClick = viewModel::markAsRead,
        modifier = modifier,
    )
}
