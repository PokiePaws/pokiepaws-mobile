package com.pokiepaws.mobile.data.remote.dto.vet

import kotlinx.serialization.Serializable

@Serializable
data class VetListResponse(
    val id: Long? = null,
    val userId: Long? = null,
    val firstName: String,
    val lastName: String,
    val specialization: String? = null,
) {
    val resolvedUserId: Long
        get() = userId ?: requireNotNull(id) { "Vet response does not contain id" }
}
