package com.pokiepaws.mobile.data.remote.dto.auth

data class UpdateOwnerPhoneRequest(
    val phoneNumber: String,
)

data class UpdateOwnerAddressRequest(
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val city: String,
    val postalCode: String,
    val country: String,
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
