package com.pokiepaws.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()

    object Loading : AuthUiState()

    data class LoginSuccess(val token: String, val role: String) : AuthUiState()

    object RegisterSuccess : AuthUiState()

    object ForgotPasswordSuccess : AuthUiState()

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
        private val _loginUiState = MutableStateFlow(LoginUiState())
        val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

        private val _loginEffects = Channel<LoginEffect>(Channel.BUFFERED)
        val loginEffects: Flow<LoginEffect> = _loginEffects.receiveAsFlow()

        fun onLoginEvent(event: LoginEvent) {
            when (event) {
                is LoginEvent.EmailChanged ->
                    _loginUiState.update { it.copy(email = event.value, errorMessage = null) }

                is LoginEvent.PasswordChanged ->
                    _loginUiState.update { it.copy(password = event.value, errorMessage = null) }

                LoginEvent.TogglePasswordVisibility ->
                    _loginUiState.update { it.copy(passwordVisible = !it.passwordVisible) }

                LoginEvent.SubmitLogin -> submitLogin()
                LoginEvent.ForgotPasswordClicked -> Unit
                LoginEvent.RegisterClicked -> Unit
            }
        }

        private fun submitLogin() {
            val current = _loginUiState.value
            val email = current.email.trim()
            val password = current.password

            if (email.isBlank() || password.isBlank()) {
                _loginUiState.update { it.copy(errorMessage = "Email and password are required") }
                return
            }

            viewModelScope.launch {
                _loginUiState.update { it.copy(isLoading = true, errorMessage = null) }

                runCatching { authRepository.login(email, password) }
                    .onSuccess { session ->
                        _loginUiState.update { it.copy(isLoading = false) }
                        _loginEffects.trySend(LoginEffect.NavigateAfterLogin(session.token, session.role))
                        _uiState.value = AuthUiState.LoginSuccess(session.token, session.role)
                    }
                    .onFailure { error ->
                        val message = error.message ?: "Login failed"
                        _loginUiState.update { it.copy(isLoading = false, errorMessage = message) }
                        _uiState.value = AuthUiState.Error(message)
                    }
            }
        }

        fun login(
            email: String,
            password: String,
        ) {
            _loginUiState.value =
                _loginUiState.value.copy(
                    email = email,
                    password = password,
                )
            onLoginEvent(LoginEvent.SubmitLogin)
        }

        fun register(registration: RegistrationDraft) {
            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading

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
                    .onSuccess { _uiState.value = AuthUiState.ForgotPasswordSuccess }
                    .onFailure {
                        _uiState.value = AuthUiState.Error("Nie udalo sie wyslac linku. Sprawdz email.")
                    }
            }
        }

        fun resetState() {
            _uiState.value = AuthUiState.Idle
            _loginUiState.value = LoginUiState()
        }
    }
