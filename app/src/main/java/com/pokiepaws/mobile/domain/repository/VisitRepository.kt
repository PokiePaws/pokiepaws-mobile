package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    fun getUpcoming(): Flow<List<Visit>>

    fun getById(visitId: Long): Flow<Visit?>

    fun getByAnimal(animalId: Long): Flow<List<Visit>>

    fun observePrescription(visitId: Long): Flow<Prescription?>

    suspend fun syncUpcoming()

    suspend fun syncById(visitId: Long)

    suspend fun syncByAnimal(animalId: Long)

    suspend fun syncPrescription(visitId: Long)

    suspend fun getAvailableSlots(
        clinicId: Long,
        vetUserId: Long,
        date: String,
    ): List<String>

    suspend fun create(visit: CreateVisitDraft): Visit

    suspend fun cancel(visitId: Long): Visit
}
