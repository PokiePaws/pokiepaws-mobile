package com.pokiepaws.mobile.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String? = null,
    val accessToken: String? = null,
    @SerialName("access_token") val accessTokenSnakeCase: String? = null,
    val jwt: String? = null,
    val email: String? = null,
    val role: String? = null,
) {
    val resolvedToken: String?
        get() = token ?: accessToken ?: accessTokenSnakeCase ?: jwt
}
