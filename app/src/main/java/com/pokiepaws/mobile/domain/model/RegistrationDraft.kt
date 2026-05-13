package com.pokiepaws.mobile.domain.model

data class RegistrationDraft(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val city: String,
    val postalCode: String,
    val country: String,
)
