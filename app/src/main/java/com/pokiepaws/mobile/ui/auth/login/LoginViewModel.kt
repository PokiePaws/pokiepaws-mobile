package com.pokiepaws.mobile.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LoginUiState())
        val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

        private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
        val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

        fun onEvent(event: LoginEvent) {
            when (event) {
                is LoginEvent.EmailChanged ->
                    _uiState.update { it.copy(email = event.value, errorMessage = null) }

                is LoginEvent.PasswordChanged ->
                    _uiState.update { it.copy(password = event.value, errorMessage = null) }

                LoginEvent.TogglePasswordVisibility ->
                    _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

                LoginEvent.SubmitLogin -> submitLogin()
                LoginEvent.ForgotPasswordClicked -> Unit
                LoginEvent.RegisterClicked -> Unit
            }
        }

        private fun submitLogin() {
            val current = _uiState.value
            val email = current.email.trim()
            val password = current.password

            if (email.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Email and password are required") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                runCatching { authRepository.login(email, password) }
                    .onSuccess { session ->
                        _uiState.update { it.copy(isLoading = false) }
                        _effects.trySend(LoginEffect.NavigateAfterLogin(session.token, session.role))
                    }
                    .onFailure { error ->
                        val message = error.message ?: "Login failed"
                        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                        _effects.trySend(LoginEffect.ShowError(message))
                    }
            }
        }
    }
