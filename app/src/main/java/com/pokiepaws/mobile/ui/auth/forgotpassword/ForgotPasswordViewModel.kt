package com.pokiepaws.mobile.ui.auth.forgotpassword

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
class ForgotPasswordViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ForgotPasswordUiState())
        val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

        private val _effects = Channel<ForgotPasswordEffect>(Channel.BUFFERED)
        val effects: Flow<ForgotPasswordEffect> = _effects.receiveAsFlow()

        fun onEvent(event: ForgotPasswordEvent) {
            when (event) {
                is ForgotPasswordEvent.EmailChanged ->
                    _uiState.update { it.copy(email = event.value, errorMessage = null) }

                ForgotPasswordEvent.Submit -> submit()
                ForgotPasswordEvent.NavigateBack -> _effects.trySend(ForgotPasswordEffect.NavigateBack)
            }
        }

        private fun submit() {
            val email = _uiState.value.email.trim()

            if (email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Enter your email address") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                runCatching { authRepository.forgotPassword(email) }
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        _effects.trySend(ForgotPasswordEffect.EmailSent(email))
                    }
                    .onFailure {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "The link could not be sent. Please check your email.",
                            )
                        }
                    }
            }
        }
    }
