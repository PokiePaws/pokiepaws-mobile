package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.VetDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.vet.VetListResponse
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.repository.VetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VetRepositoryImpl
    @Inject
    constructor(
        private val api: VetApiService,
        private val vetDao: VetDao,
    ) : VetRepository {
        override fun getByClinic(clinicId: Long): Flow<List<Vet>> =
            vetDao.getByClinic(clinicId).map { entities ->
                entities.map { it.toDomain() }
            }

        override suspend fun syncByClinic(clinicId: Long) {
            runCatching {
                api.getByClinicList(clinicId).map { it.toDomain() }
            }.onSuccess { vets ->
                vetDao.replaceClinic(
                    clinicId = clinicId,
                    vets = vets.map { it.toEntity(clinicId) },
                )
            }
        }
    }

private fun VetListResponse.toDomain(): Vet =
    Vet(
        userId = resolvedUserId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )
