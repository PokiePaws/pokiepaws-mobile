package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClinicRepositoryImpl
    @Inject
    constructor(
        private val api: ClinicApiService,
        private val clinicDao: ClinicDao,
    ) : ClinicRepository {
        override fun getClinics(): Flow<List<Clinic>> =
            clinicDao.getClinics().map { entities ->
                entities.map { it.toDomain() }
            }

        override suspend fun syncClinics() {
            runCatching {
                api.getAll().map { it.toDomain() }
            }.onSuccess { clinics ->
                clinicDao.replaceAll(clinics.map { it.toEntity() })
            }
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
