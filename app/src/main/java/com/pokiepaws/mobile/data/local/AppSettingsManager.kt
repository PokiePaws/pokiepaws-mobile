package com.pokiepaws.mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings_prefs")

@Singleton
class AppSettingsManager
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val foreignTravelPlannedKey = booleanPreferencesKey("foreign_travel_planned")

        val foreignTravelPlanned: Flow<Boolean> =
            context.appSettingsDataStore.data.map { preferences ->
                preferences[foreignTravelPlannedKey] ?: false
            }

        suspend fun setForeignTravelPlanned(value: Boolean) {
            context.appSettingsDataStore.edit { preferences ->
                preferences[foreignTravelPlannedKey] = value
            }
        }
    }
