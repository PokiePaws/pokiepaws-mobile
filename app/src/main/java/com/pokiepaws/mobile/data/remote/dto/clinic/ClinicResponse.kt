package com.pokiepaws.mobile.data.remote.dto.clinic

import kotlinx.serialization.Serializable

@Serializable
data class ClinicResponse(
    val id: Long,
    val clinicName: String,
    val city: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val country: String,
    val phone: String? = null,
    val email: String? = null,
)
