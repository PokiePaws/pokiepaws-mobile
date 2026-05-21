package com.pokiepaws.mobile.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.entities.AnimalEntity
import com.pokiepaws.mobile.data.local.room.entities.ClinicEntity
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import com.pokiepaws.mobile.data.local.room.entities.VisitEntity

@Database(
    entities = [
        NotificationEntity::class,
        AnimalEntity::class,
        ClinicEntity::class,
        VisitEntity::class,
    ],
    version = 2,
)
abstract class PokieDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    abstract fun animalDao(): AnimalDao

    abstract fun clinicDao(): ClinicDao

    abstract fun visitDao(): VisitDao
}
