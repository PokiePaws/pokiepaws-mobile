package com.pokiepaws.mobile.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 1)
abstract class PokieDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
