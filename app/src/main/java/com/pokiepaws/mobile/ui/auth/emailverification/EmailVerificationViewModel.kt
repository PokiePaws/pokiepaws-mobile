package com.pokiepaws.mobile.ui.auth.emailverification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState = MutableStateFlow(EmailVerificationUiState(email = ""))
        val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

        private val _effects = Channel<EmailVerificationEffect>(Channel.BUFFERED)
        val effects: Flow<EmailVerificationEffect> = _effects.receiveAsFlow()

        fun setEmail(email: String) {
            _uiState.update { it.copy(email = email) }
        }

        fun onEvent(event: EmailVerificationEvent) {
            when (event) {
                EmailVerificationEvent.BackToLoginClicked ->
                    _effects.trySend(EmailVerificationEffect.NavigateBackToLogin)
            }
        }
    }
