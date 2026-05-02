package com.pokiepaws.mobile.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.local.dao.NotificationDao // Upewnij się, że masz to DAO
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor(
        private val notificationDao: NotificationDao,
    ) : ViewModel() {
        // Pobieramy powiadomienia z bazy i zamieniamy na StateFlow dla Compose
        val notifications: StateFlow<List<NotificationEntity>> =
            notificationDao.getAllNotifications()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        fun markAsRead(notificationId: Int) {
            viewModelScope.launch {
                notificationDao.markAsRead(notificationId)
            }
        }

        fun markAllAsRead() {
            viewModelScope.launch {
                notificationDao.markAllAsRead()
            }
        }
    }
