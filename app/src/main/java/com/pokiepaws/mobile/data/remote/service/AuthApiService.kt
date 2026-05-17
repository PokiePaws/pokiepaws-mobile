package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.auth.AuthResponse
import com.pokiepaws.mobile.data.remote.dto.auth.ChangePasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.DeviceTokenRequest
import com.pokiepaws.mobile.data.remote.dto.auth.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.auth.MessageResponse
import com.pokiepaws.mobile.data.remote.dto.auth.RegisterRequest
import com.pokiepaws.mobile.data.remote.dto.auth.UpdateOwnerAddressRequest
import com.pokiepaws.mobile.data.remote.dto.auth.UpdateOwnerPhoneRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): AuthResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<Unit>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest,
    ): Response<MessageResponse>

    @PATCH("api/owners/me/phone")
    suspend fun updateOwnerPhone(
        @Body request: UpdateOwnerPhoneRequest,
    ): Response<Unit>

    @PATCH("api/owners/me/address")
    suspend fun updateOwnerAddress(
        @Body request: UpdateOwnerAddressRequest,
    ): Response<Unit>

    @PATCH("api/owners/me/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Unit>

    @POST("api/owners/me/device-tokens")
    suspend fun registerDeviceToken(
        @Body request: DeviceTokenRequest,
    ): Response<Unit>
}
