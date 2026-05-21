package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import java.io.IOException
import javax.inject.Inject

class ClinicRepositoryImpl
    @Inject
    constructor(
        private val api: ClinicApiService,
        private val clinicDao: ClinicDao,
    ) : ClinicRepository {
        override suspend fun getClinics(): List<Clinic> =
            runCatching {
                api.getAll().map { it.toDomain() }
            }.onSuccess { clinics ->
                clinicDao.clear()
                clinicDao.upsertClinics(clinics.map { it.toEntity() })
            }.getOrElse { error ->
                val cached = clinicDao.getClinics().map { it.toDomain() }
                if (cached.isNotEmpty() && error is IOException) cached else throw error
            }
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
