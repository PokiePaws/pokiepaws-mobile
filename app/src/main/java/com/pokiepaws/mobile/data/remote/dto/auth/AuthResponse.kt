package com.pokiepaws.mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val email: String,
    val role: String,
)
