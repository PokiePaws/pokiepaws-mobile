package com.pokiepaws.mobile.data.local

import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataCleaner
    @Inject
    constructor(
        private val animalDao: AnimalDao,
        private val clinicDao: ClinicDao,
        private val visitDao: VisitDao,
        private val notificationDao: NotificationDao,
    ) {
        suspend fun clearSensitiveData() {
            animalDao.clear()
            clinicDao.clear()
            visitDao.clear()
            notificationDao.clear()
        }
    }
