package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.data.remote.dto.visit.VisitResponse
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.VisitRepository
import java.io.IOException
import javax.inject.Inject

class VisitRepositoryImpl
    @Inject
    constructor(
        private val api: VisitApiService,
        private val visitDao: VisitDao,
    ) : VisitRepository {
        override suspend fun getUpcoming(): List<Visit> =
            getNetworkFirstVisits(
                scope = UPCOMING_VISITS_SCOPE,
                fetch = { api.getUpcomingOwnerVisits().map { it.toDomain() } },
            )

        override suspend fun cancel(visitId: Long): Visit =
            api.cancelVisit(visitId).toDomain().also { visit ->
                visitDao.upsertVisits(
                    listOf(
                        visit.toEntity(UPCOMING_VISITS_SCOPE),
                        visit.toEntity(animalVisitsScope(visit.animalId)),
                    ),
                )
            }

        override suspend fun getById(visitId: Long): Visit =
            runCatching {
                api.getVisitById(visitId).toDomain()
            }.onSuccess { visit ->
                visitDao.upsertVisits(listOf(visit.toEntity(DETAIL_VISITS_SCOPE)))
            }.getOrElse { error ->
                val cached = visitDao.getVisit(visitId)?.toDomain()
                if (cached != null && error is IOException) cached else throw error
            }

        override suspend fun getByAnimal(animalId: Long): List<Visit> =
            getNetworkFirstVisits(
                scope = animalVisitsScope(animalId),
                fetch = { api.getVisitsByAnimal(animalId).map { it.toDomain() } },
            )

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

        override suspend fun create(visit: CreateVisitDraft): Visit =
            api.createVisit(visit.toRequest()).toDomain().also { createdVisit ->
                visitDao.upsertVisits(
                    listOf(
                        createdVisit.toEntity(UPCOMING_VISITS_SCOPE),
                        createdVisit.toEntity(animalVisitsScope(createdVisit.animalId)),
                    ),
                )
            }

        private suspend fun getNetworkFirstVisits(
            scope: String,
            fetch: suspend () -> List<Visit>,
        ): List<Visit> =
            runCatching {
                fetch()
            }.onSuccess { visits ->
                visitDao.clearScope(scope)
                visitDao.upsertVisits(visits.map { it.toEntity(scope) })
            }.getOrElse { error ->
                val cached = visitDao.getVisits(scope).map { it.toDomain() }
                if (cached.isNotEmpty() && error is IOException) cached else throw error
            }
    }

private const val UPCOMING_VISITS_SCOPE = "upcoming"
private const val DETAIL_VISITS_SCOPE = "detail"

private fun animalVisitsScope(animalId: Long): String = "animal:$animalId"

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
