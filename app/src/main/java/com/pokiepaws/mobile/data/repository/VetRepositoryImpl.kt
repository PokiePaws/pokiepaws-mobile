package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.vet.VetListResponse
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.repository.VetRepository
import javax.inject.Inject

class VetRepositoryImpl
    @Inject
    constructor(
        private val api: VetApiService,
    ) : VetRepository {
        override suspend fun getByClinic(clinicId: Long): List<Vet> = api.getByClinicList(clinicId).map { it.toDomain() }
    }

private fun VetListResponse.toDomain(): Vet =
    Vet(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )
