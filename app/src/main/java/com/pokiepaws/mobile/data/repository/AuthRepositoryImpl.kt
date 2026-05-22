package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.LocalDataCleaner
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.local.dao.OwnerProfileDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.auth.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.auth.RefreshTokenRequest
import com.pokiepaws.mobile.data.remote.dto.auth.RegisterRequest
import com.pokiepaws.mobile.data.remote.dto.settings.ChangePasswordRequest
import com.pokiepaws.mobile.data.remote.dto.settings.OwnerProfileResponse
import com.pokiepaws.mobile.data.remote.dto.settings.UpdateOwnerAddressRequest
import com.pokiepaws.mobile.data.remote.dto.settings.UpdateOwnerPhoneNumberRequest
import com.pokiepaws.mobile.data.remote.service.AuthApiService
import com.pokiepaws.mobile.domain.model.AuthSession
import com.pokiepaws.mobile.domain.model.OwnerAddressDraft
import com.pokiepaws.mobile.domain.model.OwnerPhoneDraft
import com.pokiepaws.mobile.domain.model.OwnerProfile
import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authApiService: AuthApiService,
        private val tokenManager: TokenManager,
        private val localDataCleaner: LocalDataCleaner,
        private val ownerProfileDao: OwnerProfileDao,
    ) : AuthRepository {
        override val token: Flow<String?> = tokenManager.token

        override suspend fun login(
            email: String,
            password: String,
        ): AuthSession {
            val response = authApiService.login(LoginRequest(email, password))
            val token = requireNotNull(response.resolvedToken) { "Login response does not contain access token" }
            return AuthSession(
                token = token,
                role = response.role.orEmpty(),
            ).also {
                tokenManager.saveTokens(
                    accessToken = token,
                    refreshToken = response.resolvedRefreshToken,
                )
            }
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

        override suspend fun getCurrentOwnerProfile(): OwnerProfile =
            runCatching {
                authApiService.getCurrentOwnerProfile().toDomain()
            }.onSuccess { profile ->
                ownerProfileDao.upsertProfile(profile.toEntity())
            }.getOrElse { error ->
                val cached = ownerProfileDao.getProfile()?.toDomain()
                if (cached != null && error is IOException) cached else throw error
            }

        override suspend fun updateOwnerPhone(phone: OwnerPhoneDraft) {
            val response = authApiService.updateOwnerPhone(phone.toRequest())
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            ownerProfileDao.updatePhoneNumber(phone.phoneNumber)
        }

        override suspend fun updateOwnerAddress(address: OwnerAddressDraft) {
            val response = authApiService.updateOwnerAddress(address.toRequest())
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            ownerProfileDao.updateAddress(
                street = address.street,
                houseNumber = address.houseNumber,
                apartmentNumber = address.apartmentNumber,
                city = address.city,
                postalCode = address.postalCode,
                country = address.country,
            )
        }

        override suspend fun changePassword(
            currentPassword: String,
            newPassword: String,
        ) {
            val response = authApiService.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }

        override suspend fun logout() {
            tokenManager.refreshToken.first()?.let { refreshToken ->
                runCatching { authApiService.logout(RefreshTokenRequest(refreshToken)) }
            }
            localDataCleaner.clearSensitiveData()
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

private fun OwnerPhoneDraft.toRequest(): UpdateOwnerPhoneNumberRequest =
    UpdateOwnerPhoneNumberRequest(
        phoneNumber = phoneNumber,
    )

private fun OwnerAddressDraft.toRequest(): UpdateOwnerAddressRequest =
    UpdateOwnerAddressRequest(
        street = street,
        houseNumber = houseNumber,
        apartmentNumber = apartmentNumber,
        city = city,
        postalCode = postalCode,
        country = country,
    )

private fun OwnerProfileResponse.toDomain(): OwnerProfile =
    OwnerProfile(
        userId = userId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        street = street,
        houseNumber = houseNumber,
        apartmentNumber = apartmentNumber,
        postalCode = postalCode,
        city = city,
        country = country,
    )
