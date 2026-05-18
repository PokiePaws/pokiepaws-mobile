package com.pokiepaws.mobile.ui.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())

    NotificationContent(
        notifications = NotificationItems(notifications),
        onBack = onBack,
        onMarkAllAsRead = viewModel::markAllAsRead,
        modifier = modifier,
    )
}
