package com.pokiepaws.mobile.data.sync

import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.clinic.toDomain
import com.pokiepaws.mobile.data.remote.dto.visit.toDomain
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import kotlinx.coroutines.flow.first
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

                animals.forEach { animal ->
                    val scope = animalVisitsScope(animal.id)
                    val visits = api.visitApiService.getVisitsByAnimal(animal.id).map { it.toDomain() }
                    dao.visitDao.replaceScope(
                        scope = scope,
                        visits = visits.map { it.toEntity(scope) },
                    )
                }
            }
        }
    }

private const val UPCOMING_VISITS_SCOPE = "upcoming"

private fun animalVisitsScope(animalId: Long): String = "animal:$animalId"
