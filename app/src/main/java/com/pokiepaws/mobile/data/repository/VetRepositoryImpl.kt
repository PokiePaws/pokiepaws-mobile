package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.VetDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.vet.VetListResponse
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.repository.VetRepository
import java.io.IOException
import javax.inject.Inject

class VetRepositoryImpl
    @Inject
    constructor(
        private val api: VetApiService,
        private val vetDao: VetDao,
    ) : VetRepository {
        override suspend fun getByClinic(clinicId: Long): List<Vet> =
            runCatching {
                api.getByClinicList(clinicId).map { it.toDomain() }
            }.onSuccess { vets ->
                vetDao.clearClinic(clinicId)
                vetDao.upsertVets(vets.map { it.toEntity(clinicId) })
            }.getOrElse { error ->
                val cached = vetDao.getByClinic(clinicId).map { it.toDomain() }
                if (cached.isNotEmpty() && error is IOException) cached else throw error
            }
    }

private fun VetListResponse.toDomain(): Vet =
    Vet(
        userId = resolvedUserId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )
