package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.Visit

interface VisitRepository {
    suspend fun getUpcoming(): List<Visit>

    suspend fun getById(visitId: Long): Visit

    suspend fun getByAnimal(animalId: Long): List<Visit>

    suspend fun getAvailableSlots(
        clinicId: Long,
        vetUserId: Long,
        date: String,
    ): List<String>

    suspend fun create(visit: CreateVisitDraft): Visit

    suspend fun cancel(visitId: Long): Visit
}
