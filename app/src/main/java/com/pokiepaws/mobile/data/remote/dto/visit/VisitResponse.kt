package com.pokiepaws.mobile.data.remote.dto.visit

import kotlinx.serialization.Serializable

@Serializable
data class VisitResponse(
    val id: Long,
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val endsAt: String,
    val description: String? = null,
    val disease: String? = null,
    val diagnosis: String? = null,
    val recommendations: String? = null,
    val status: String,
    val used: Boolean,
)
