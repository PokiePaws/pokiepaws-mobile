package com.pokiepaws.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()

    object Loading : AuthUiState()

    data class LoginSuccess(val token: String, val role: String) : AuthUiState()

    object RegisterSuccess : AuthUiState()

    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        fun login(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading
                runCatching { authRepository.login(email, password) }
                    .onSuccess { session ->
                        _uiState.value = AuthUiState.LoginSuccess(session.token, session.role)
                    }
                    .onFailure { error ->
                        _uiState.value = AuthUiState.Error(error.message ?: "Blad logowania")
                    }
            }
        }

        fun register(
            email: String,
            password: String,
            firstName: String,
            lastName: String,
            phoneNumber: String,
            street: String,
            houseNumber: String,
            apartmentNumber: String?,
            city: String,
            postalCode: String,
            country: String,
        ) {
            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading
                val registration =
                    RegistrationDraft(
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

                runCatching { authRepository.register(registration) }
                    .onSuccess { _uiState.value = AuthUiState.RegisterSuccess }
                    .onFailure { error ->
                        _uiState.value = AuthUiState.Error(error.message ?: "Blad polaczenia z serwerem")
                    }
            }
        }

        fun forgotPassword(email: String) {
            if (email.isBlank()) {
                _uiState.value = AuthUiState.Error("Wpisz adres email")
                return
            }

            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading
                runCatching { authRepository.forgotPassword(email) }
                    .onSuccess { _uiState.value = AuthUiState.Idle }
                    .onFailure {
                        _uiState.value = AuthUiState.Error("Nie udalo sie wyslac linku. Sprawdz email.")
                    }
            }
        }

        fun resetState() {
            _uiState.value = AuthUiState.Idle
        }
    }
