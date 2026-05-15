package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Clinic

fun interface ClinicRepository {
    suspend fun getClinics(): List<Clinic>
}
