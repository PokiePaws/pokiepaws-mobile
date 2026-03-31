package com.pokiepaws.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.remote.AuthApiService
import com.pokiepaws.mobile.data.remote.LoginRequest
import com.pokiepaws.mobile.data.remote.RegisterRequest
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
class AuthViewModel @Inject constructor(
    private val authApiService: AuthApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = authApiService.login(LoginRequest(email, password))
                _uiState.value = AuthUiState.LoginSuccess(response.token, response.role)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Błąd logowania")
            }
        }
    }

    fun register(
        email: String, password: String, firstName: String, lastName: String,
        phoneNumber: String, street: String, houseNumber: String,
        apartmentNumber: String?, city: String, postalCode: String, country: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = authApiService.register(
                    RegisterRequest(
                        email, password, firstName, lastName, phoneNumber,
                        street, houseNumber, apartmentNumber, city, postalCode, country
                    )
                )

                if (response.isSuccessful) {
                    _uiState.value = AuthUiState.RegisterSuccess
                } else {
                    _uiState.value = AuthUiState.Error("Błąd: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Błąd połączenia z serwerem")
            }
        }
    }
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}