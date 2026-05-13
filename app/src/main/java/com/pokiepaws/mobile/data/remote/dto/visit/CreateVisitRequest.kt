package com.pokiepaws.mobile.data.remote.dto.visit

import kotlinx.serialization.Serializable

@Serializable
data class CreateVisitRequest(
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val description: String? = null,
)
