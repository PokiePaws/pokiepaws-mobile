package com.pokiepaws.mobile.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)
