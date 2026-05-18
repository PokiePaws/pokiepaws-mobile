package com.pokiepaws.mobile.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val foreignTravelPlanned: Flow<Boolean>

    suspend fun setForeignTravelPlanned(value: Boolean)
}
