package com.pokiepaws.mobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ProfileUiState())
        val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

        init {
            observeProfile()
            syncProfile()
        }

        private fun observeProfile() {
            viewModelScope.launch {
                authRepository.observeProfile().collect { profile ->
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                displayName = profile.displayName.ifBlank { profile.email },
                                email = profile.email,
                                initials = profile.initials,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
        }

        fun syncProfile() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                runCatching { authRepository.syncProfile() }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Server connection error",
                            )
                        }
                    }
            }
        }

        fun loadProfile() {
            syncProfile()
        }
    }
