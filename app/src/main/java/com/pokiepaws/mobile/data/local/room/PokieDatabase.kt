package com.pokiepaws.mobile.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.dao.OwnerProfileDao
import com.pokiepaws.mobile.data.local.dao.VetDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.entities.AnimalEntity
import com.pokiepaws.mobile.data.local.room.entities.ClinicEntity
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
import com.pokiepaws.mobile.data.local.room.entities.OwnerProfileEntity
import com.pokiepaws.mobile.data.local.room.entities.VetEntity
import com.pokiepaws.mobile.data.local.room.entities.VisitEntity

@Database(
    entities = [
        NotificationEntity::class,
        AnimalEntity::class,
        ClinicEntity::class,
        VisitEntity::class,
        VetEntity::class,
        OwnerProfileEntity::class,
    ],
    version = 3,
)
abstract class PokieDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    abstract fun animalDao(): AnimalDao

    abstract fun clinicDao(): ClinicDao

    abstract fun visitDao(): VisitDao

    abstract fun vetDao(): VetDao

    abstract fun ownerProfileDao(): OwnerProfileDao
}
