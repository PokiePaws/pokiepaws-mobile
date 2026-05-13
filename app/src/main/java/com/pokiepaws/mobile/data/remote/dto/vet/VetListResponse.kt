package com.pokiepaws.mobile.data.remote.dto.vet

import kotlinx.serialization.Serializable

@Serializable
data class VetListResponse(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val specialization: String? = null,
)