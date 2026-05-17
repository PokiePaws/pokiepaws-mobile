package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.AppSettingsManager
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingsRepositoryImpl
    @Inject
    constructor(
        private val appSettingsManager: AppSettingsManager,
    ) : AppSettingsRepository {
        override val foreignTravelPlanned: Flow<Boolean> = appSettingsManager.foreignTravelPlanned

        override suspend fun setForeignTravelPlanned(value: Boolean) {
            appSettingsManager.setForeignTravelPlanned(value)
        }
    }
