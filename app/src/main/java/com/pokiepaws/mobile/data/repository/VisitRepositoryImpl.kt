package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.data.remote.dto.visit.VisitResponse
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.repository.VisitRepository
import javax.inject.Inject

class VisitRepositoryImpl @Inject constructor(
    private val api: VisitApiService,
) : VisitRepository {

    override suspend fun getUpcoming(): List<Visit> =
        api.getUpcomingOwnerVisits().map { it.toDomain() }

    override suspend fun cancel(visitId: Long): Visit =
        api.cancelVisit(visitId).toDomain()

    override suspend fun getById(visitId: Long): Visit =
        api.getVisitById(visitId).toDomain()

    override suspend fun create(request: CreateVisitRequest): Visit =
        api.createVisit(request).toDomain()
}

private fun VisitResponse.toDomain(): Visit =
    Visit(
        id = id,
        animalId = animalId,
        clinicId = clinicId,
        vetUserId = vetUserId,
        startsAt = startsAt,
        endsAt = endsAt,
        description = description,
        disease = disease,
        diagnosis = diagnosis,
        recommendations = recommendations,
        status = status,
    )
