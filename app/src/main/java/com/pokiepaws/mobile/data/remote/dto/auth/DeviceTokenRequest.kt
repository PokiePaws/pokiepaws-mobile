package com.pokiepaws.mobile.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(
    val token: String,
    val platform: String = "ANDROID",
)
