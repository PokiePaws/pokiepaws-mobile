package com.pokiepaws.mobile.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String? = null,
    val accessToken: String? = null,
    @SerialName("access_token") val accessTokenSnakeCase: String? = null,
    val refreshToken: String? = null,
    @SerialName("refresh_token") val refreshTokenSnakeCase: String? = null,
    val jwt: String? = null,
    val email: String? = null,
    val role: String? = null,
    val mfaRequired: Boolean = false,
) {
    val resolvedToken: String?
        get() = token ?: accessToken ?: accessTokenSnakeCase ?: jwt

    val resolvedRefreshToken: String?
        get() = refreshToken ?: refreshTokenSnakeCase
}
