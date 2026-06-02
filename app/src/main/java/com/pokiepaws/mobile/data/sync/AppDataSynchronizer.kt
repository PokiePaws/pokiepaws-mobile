package com.pokiepaws.mobile.data.sync

import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.PrescriptionDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.local.room.mappers.toItemEntities
import com.pokiepaws.mobile.data.remote.dto.clinic.toDomain
import com.pokiepaws.mobile.data.remote.dto.visit.PrescriptionResponse
import com.pokiepaws.mobile.data.remote.dto.visit.toDomain
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.pokiepaws.mobile.data.remote.dto.animal.toDomain as animalResponseToDomain

@Singleton
class AppDataSynchronizer
    @Inject
    constructor(
        private val tokenManager: TokenManager,
        private val api: Api,
        private val dao: Dao,
    ) {
        class Api
            @Inject
            constructor(
                val animalApiService: AnimalApiService,
                val clinicApiService: ClinicApiService,
                val visitApiService: VisitApiService,
            )

        class Dao
            @Inject
            constructor(
                val animalDao: AnimalDao,
                val clinicDao: ClinicDao,
                val visitDao: VisitDao,
                val prescriptionDao: PrescriptionDao,
            )

        suspend fun syncReadableCache() {
            if (tokenManager.token.first().isNullOrBlank()) return

            runCatching {
                val animals = api.animalApiService.getMyAnimals().map { it.animalResponseToDomain() }
                dao.animalDao.replaceAll(animals.map { it.toEntity() })

                val clinics = api.clinicApiService.getAll().map { it.toDomain() }
                dao.clinicDao.replaceAll(clinics.map { it.toEntity() })

                val upcomingVisits = api.visitApiService.getUpcomingOwnerVisits().map { it.toDomain() }
                dao.visitDao.replaceScope(
                    scope = UPCOMING_VISITS_SCOPE,
                    visits = upcomingVisits.map { it.toEntity(UPCOMING_VISITS_SCOPE) },
                )
                syncPrescriptions(upcomingVisits)

                animals.forEach { animal ->
                    val scope = animalVisitsScope(animal.id)
                    val visits = api.visitApiService.getVisitsByAnimal(animal.id).map { it.toDomain() }
                    dao.visitDao.replaceScope(
                        scope = scope,
                        visits = visits.map { it.toEntity(scope) },
                    )
                    syncPrescriptions(visits)
                }
            }
        }

        private suspend fun syncPrescriptions(visits: List<Visit>) {
            visits.forEach { visit ->
                runCatching {
                    val prescription = api.visitApiService.getPrescription(visit.id).toPrescriptionOrNull()
                    dao.prescriptionDao.replaceForVisit(
                        visitId = visit.id,
                        prescription = prescription?.toEntity(),
                        items = prescription?.toItemEntities().orEmpty(),
                    )
                }
            }
        }

        private fun Response<PrescriptionResponse>.toPrescriptionOrNull(): Prescription? {
            if (code() == HTTP_NOT_FOUND || code() == HTTP_NO_CONTENT) return null
            if (!isSuccessful) throw HttpException(this)
            return body()?.toDomain()
        }
    }

private const val UPCOMING_VISITS_SCOPE = "upcoming"
private const val HTTP_NOT_FOUND = 404
private const val HTTP_NO_CONTENT = 204

private fun animalVisitsScope(animalId: Long): String = "animal:$animalId"
