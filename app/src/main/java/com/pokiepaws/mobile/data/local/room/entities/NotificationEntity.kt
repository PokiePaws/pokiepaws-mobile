package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: String? = null, // "reminder" | "vaccine" | "success" | null (fallback)
)
