package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.remote.dto.auth.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.auth.RegisterRequest
import com.pokiepaws.mobile.data.remote.service.AuthApiService
import com.pokiepaws.mobile.domain.model.AuthSession
import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authApiService: AuthApiService,
        private val tokenManager: TokenManager,
    ) : AuthRepository {
        override val token: Flow<String?> = tokenManager.token

        override suspend fun login(
            email: String,
            password: String,
        ): AuthSession {
            val response = authApiService.login(LoginRequest(email, password))
            tokenManager.saveToken(response.token)
            return AuthSession(
                token = response.token,
                role = response.role,
            )
        }

        override suspend fun register(registration: RegistrationDraft) {
            val response = authApiService.register(registration.toRequest())
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }

        override suspend fun forgotPassword(email: String) {
            val response = authApiService.forgotPassword(ForgotPasswordRequest(email))
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }

        override suspend fun logout() {
            tokenManager.clearToken()
        }
    }

private fun RegistrationDraft.toRequest(): RegisterRequest =
    RegisterRequest(
        email = email,
        password = password,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        street = street,
        houseNumber = houseNumber,
        apartmentNumber = apartmentNumber,
        city = city,
        postalCode = postalCode,
        country = country,
    )
