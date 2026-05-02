package com.pokiepaws.mobile.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.remote.dto.auth.ForgotPasswordRequest
import com.pokiepaws.mobile.data.remote.dto.auth.LoginRequest
import com.pokiepaws.mobile.data.remote.dto.auth.RegisterRequest
import com.pokiepaws.mobile.data.remote.service.AuthApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
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
        private val authApiService: AuthApiService,
        private val tokenManager: TokenManager,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        fun login(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading
                try {
                    val response = authApiService.login(LoginRequest(email, password))
                    tokenManager.saveToken(response.token)
                    _uiState.value = AuthUiState.LoginSuccess(response.token, response.role)
                } catch (e: HttpException) {
                    Log.e("AuthViewModel", "Błąd HTTP przy logowaniu", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Błąd logowania")
                } catch (e: IOException) {
                    Log.e("AuthViewModel", "Błąd połączenia przy logowaniu", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Błąd połączenia z serwerem")
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
                try {
                    val response =
                        authApiService.register(
                            RegisterRequest(
                                email, password, firstName, lastName, phoneNumber,
                                street, houseNumber, apartmentNumber, city, postalCode, country,
                            ),
                        )
                    if (response.isSuccessful) {
                        _uiState.value = AuthUiState.RegisterSuccess
                    } else {
                        _uiState.value = AuthUiState.Error("Błąd: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    Log.e("AuthViewModel", "Błąd HTTP przy rejestracji", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Błąd połączenia z serwerem")
                } catch (e: IOException) {
                    Log.e("AuthViewModel", "Błąd połączenia przy rejestracji", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Błąd połączenia z serwerem")
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
                try {
                    val response = authApiService.forgotPassword(ForgotPasswordRequest(email))
                    if (response.isSuccessful) {
                        _uiState.value = AuthUiState.Idle
                    } else {
                        _uiState.value = AuthUiState.Error("Nie udało się wysłać linku. Sprawdź email.")
                    }
                } catch (e: HttpException) {
                    Log.e("AuthViewModel", "Błąd HTTP przy resetowaniu hasła", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Błąd serwera")
                } catch (e: IOException) {
                    Log.e("AuthViewModel", "Błąd połączenia przy resetowaniu hasła", e)
                    _uiState.value = AuthUiState.Error("Błąd połączenia z serwerem.")
                }
            }
        }

        fun resetState() {
            _uiState.value = AuthUiState.Idle
        }
    }
