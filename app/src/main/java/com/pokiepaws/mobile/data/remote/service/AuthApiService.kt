package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.auth.AuthResponse
import com.pokiepaws.mobile.data.remote.dto.auth.DeviceTokenRequest
import com.pokiepaws.mobile.data.remote.dto.auth.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.auth.MessageResponse
import com.pokiepaws.mobile.data.remote.dto.auth.RefreshTokenRequest
import com.pokiepaws.mobile.data.remote.dto.auth.RegisterRequest
import com.pokiepaws.mobile.data.remote.dto.settings.ChangePasswordRequest
import com.pokiepaws.mobile.data.remote.dto.settings.OwnerProfileResponse
import com.pokiepaws.mobile.data.remote.dto.settings.UpdateOwnerAddressRequest
import com.pokiepaws.mobile.data.remote.dto.settings.UpdateOwnerPhoneNumberRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): AuthResponse

    @POST("api/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest,
    ): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest,
    ): Response<Unit>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<Unit>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest,
    ): Response<MessageResponse>

    @GET("api/owners/me")
    suspend fun getCurrentOwnerProfile(): OwnerProfileResponse

    @PATCH("api/owners/me/phone")
    suspend fun updateOwnerPhone(
        @Body request: UpdateOwnerPhoneNumberRequest,
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
