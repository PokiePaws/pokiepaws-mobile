package com.pokiepaws.mobile.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.AppNotification
import com.pokiepaws.mobile.domain.repository.NotificationRepository
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
        private val notificationRepository: NotificationRepository,
    ) : ViewModel() {
        val notifications: StateFlow<List<AppNotification>> =
            notificationRepository.notifications
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        fun markAsRead(notificationId: Int) {
            viewModelScope.launch {
                notificationRepository.markAsRead(notificationId)
            }
        }

        fun markAllAsRead() {
            viewModelScope.launch {
                notificationRepository.markAllAsRead()
            }
        }
    }
