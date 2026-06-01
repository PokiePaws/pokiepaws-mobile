package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Clinic
import kotlinx.coroutines.flow.Flow

interface ClinicRepository {
    fun getClinics(): Flow<List<Clinic>>

    suspend fun syncClinics()
}
