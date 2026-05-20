package com.pokiepaws.mobile.data.remote.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class OwnerProfileResponse(
    val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String? = null,
    val postalCode: String,
    val city: String,
    val country: String,
)
