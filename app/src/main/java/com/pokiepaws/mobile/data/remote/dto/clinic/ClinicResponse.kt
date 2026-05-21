package com.pokiepaws.mobile.data.remote.dto.clinic

import com.pokiepaws.mobile.domain.model.Clinic
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

fun ClinicResponse.toDomain(): Clinic =
    Clinic(
        id = id,
        clinicName = clinicName,
        city = city,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        country = country,
        phone = phone,
        email = email,
    )
