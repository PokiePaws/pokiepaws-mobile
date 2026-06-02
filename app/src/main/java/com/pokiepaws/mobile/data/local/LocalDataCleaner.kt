package com.pokiepaws.mobile.data.local

import com.pokiepaws.mobile.data.local.room.PokieDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataCleaner
    @Inject
    constructor(
        private val database: PokieDatabase,
    ) {
        suspend fun clearSensitiveData() {
            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }
        }
    }
