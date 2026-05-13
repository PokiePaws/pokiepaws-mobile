package com.pokiepaws.mobile.domain.model

data class Clinic(
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
