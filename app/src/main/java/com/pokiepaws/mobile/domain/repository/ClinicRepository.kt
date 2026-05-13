package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Clinic

interface ClinicRepository {
    suspend fun getClinics(): List<Clinic>
}
