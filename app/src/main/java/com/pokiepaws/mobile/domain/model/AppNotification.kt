package com.pokiepaws.mobile.domain.model

data class AppNotification(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: String?,
)
