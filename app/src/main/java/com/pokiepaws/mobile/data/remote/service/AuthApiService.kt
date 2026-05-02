package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.AuthResponse
import com.pokiepaws.mobile.data.remote.dto.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.MessageResponse
import com.pokiepaws.mobile.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
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
}
