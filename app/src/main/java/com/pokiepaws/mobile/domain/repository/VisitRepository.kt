package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.domain.model.Visit

interface VisitRepository {
    suspend fun getUpcoming(): List<Visit>

    suspend fun getById(visitId: Long): Visit

    suspend fun create(request: CreateVisitRequest): Visit

    suspend fun cancel(visitId: Long): Visit
}
