package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import javax.inject.Inject

class ClinicRepositoryImpl
    @Inject
    constructor(
        private val api: ClinicApiService,
    ) : ClinicRepository {
        override suspend fun getClinics(): List<Clinic> = api.getAll().map { it.toDomain() }
    }

private fun ClinicResponse.toDomain(): Clinic =
    Clinic(
        id = id,
        clinicName = clinicName,
        city = city,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        country = country,
        phone = phone,
        email = email,
    )
