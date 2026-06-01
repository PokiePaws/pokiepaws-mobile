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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VisitRepositoryImpl
    @Inject
    constructor(
        private val api: VisitApiService,
        private val visitDao: VisitDao,
    ) : VisitRepository {
        override fun getUpcoming(): Flow<List<Visit>> =
            visitDao.getVisits(UPCOMING_VISITS_SCOPE).map { entities ->
                entities.map { it.toDomain() }
            }

        override fun getById(visitId: Long): Flow<Visit?> = visitDao.getVisit(visitId).map { it?.toDomain() }

        override fun getByAnimal(animalId: Long): Flow<List<Visit>> =
            visitDao.getVisits(animalVisitsScope(animalId)).map { entities ->
                entities.map { it.toDomain() }
            }

        override suspend fun syncUpcoming() {
            runCatching {
                api.getUpcomingOwnerVisits().map { it.toDomain() }
            }.onSuccess { visits ->
                visitDao.replaceScope(
                    scope = UPCOMING_VISITS_SCOPE,
                    visits = visits.map { it.toEntity(UPCOMING_VISITS_SCOPE) },
                )
            }
        }

        override suspend fun syncById(visitId: Long) {
            runCatching {
                api.getVisitById(visitId).toDomain()
            }.onSuccess { visit ->
                visitDao.upsertVisits(listOf(visit.toEntity(DETAIL_VISITS_SCOPE)))
            }
        }

        override suspend fun syncByAnimal(animalId: Long) {
            val scope = animalVisitsScope(animalId)
            runCatching {
                api.getVisitsByAnimal(animalId).map { it.toDomain() }
            }.onSuccess { visits ->
                visitDao.replaceScope(
                    scope = scope,
                    visits = visits.map { it.toEntity(scope) },
                )
            }
        }

        override suspend fun cancel(visitId: Long): Visit =
            api.cancelVisit(visitId).toDomain().also { visit ->
                visitDao.upsertVisits(
                    listOf(
                        visit.toEntity(UPCOMING_VISITS_SCOPE),
                        visit.toEntity(animalVisitsScope(visit.animalId)),
                    ),
                )
            }

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
