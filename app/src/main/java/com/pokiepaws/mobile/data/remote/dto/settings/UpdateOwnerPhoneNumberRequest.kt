package com.pokiepaws.mobile.data.remote.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOwnerPhoneNumberRequest(
    val phoneNumber: String,
)
