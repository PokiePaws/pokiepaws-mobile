package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.AuthSession
import com.pokiepaws.mobile.domain.model.OwnerAddressDraft
import com.pokiepaws.mobile.domain.model.OwnerPhoneDraft
import com.pokiepaws.mobile.domain.model.RegistrationDraft
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val token: Flow<String?>

    suspend fun login(
        email: String,
        password: String,
    ): AuthSession

    suspend fun register(registration: RegistrationDraft)

    suspend fun forgotPassword(email: String)

    suspend fun updateOwnerPhone(phone: OwnerPhoneDraft)

    suspend fun updateOwnerAddress(address: OwnerAddressDraft)

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    )

    suspend fun logout()
}
