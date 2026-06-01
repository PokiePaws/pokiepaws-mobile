package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Vet
import kotlinx.coroutines.flow.Flow

interface VetRepository {
    fun getByClinic(clinicId: Long): Flow<List<Vet>>

    suspend fun syncByClinic(clinicId: Long)
}
