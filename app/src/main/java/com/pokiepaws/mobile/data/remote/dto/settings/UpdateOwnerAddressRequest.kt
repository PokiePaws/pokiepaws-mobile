package com.pokiepaws.mobile.data.remote.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOwnerAddressRequest(
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val city: String,
    val postalCode: String,
    val country: String,
)
