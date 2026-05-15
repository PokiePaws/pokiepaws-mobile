package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Vet

fun interface VetRepository {
    suspend fun getByClinic(clinicId: Long): List<Vet>
}
