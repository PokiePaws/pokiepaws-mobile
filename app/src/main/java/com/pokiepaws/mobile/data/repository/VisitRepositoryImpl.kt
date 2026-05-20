package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.data.remote.dto.visit.VisitResponse
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.VisitRepository
import javax.inject.Inject

class VisitRepositoryImpl
    @Inject
    constructor(
        private val api: VisitApiService,
    ) : VisitRepository {
        override suspend fun getUpcoming(): List<Visit> = api.getUpcomingOwnerVisits().map { it.toDomain() }

        override suspend fun cancel(visitId: Long): Visit = api.cancelVisit(visitId).toDomain()

        override suspend fun getById(visitId: Long): Visit = api.getVisitById(visitId).toDomain()

        override suspend fun getByAnimal(animalId: Long): List<Visit> = api.getVisitsByAnimal(animalId).map { it.toDomain() }

        override suspend fun getAvailableSlots(
            clinicId: Long,
            vetUserId: Long,
            date: String,
        ): List<String> =
            api.getAvailableSlots(
                clinicId = clinicId,
                vetUserId = vetUserId,
                date = date,
            ).availableStarts

        override suspend fun create(visit: CreateVisitDraft): Visit = api.createVisit(visit.toRequest()).toDomain()
    }

private fun CreateVisitDraft.toRequest(): CreateVisitRequest =
    CreateVisitRequest(
        animalId = animalId,
        clinicId = clinicId,
        vetUserId = vetUserId,
        startsAt = startsAt,
        description = description.name,
    )

private fun VisitResponse.toDomain(): Visit =
    Visit(
        id = id,
        animalId = animalId,
        clinicId = clinicId,
        vetUserId = vetUserId,
        startsAt = startsAt,
        endsAt = endsAt,
        description = description.toVisitDescriptionOrDefault(),
        disease = disease,
        diagnosis = diagnosis,
        recommendations = recommendations,
        status = status,
    )

private fun String?.toVisitDescriptionOrDefault(): VisitDescription =
    this
        ?.let { value -> runCatching { VisitDescription.valueOf(value) }.getOrNull() }
        ?: VisitDescription.CHECKUP
