package com.pokiepaws.mobile.domain.model

data class OwnerPhoneDraft(
    val phoneNumber: String,
)

data class OwnerAddressDraft(
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val city: String,
    val postalCode: String,
    val country: String,
)
