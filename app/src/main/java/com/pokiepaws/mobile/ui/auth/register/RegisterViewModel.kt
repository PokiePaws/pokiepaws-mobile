package com.pokiepaws.mobile.ui.auth.register

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
class RegisterViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterUiState())
        val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

        private val _effects = Channel<RegisterEffect>(Channel.BUFFERED)
        val effects: Flow<RegisterEffect> = _effects.receiveAsFlow()

        fun onEvent(event: RegisterEvent) {
            when (event) {
                is RegisterEvent.EmailChanged,
                is RegisterEvent.PasswordChanged,
                is RegisterEvent.ConfirmPasswordChanged,
                is RegisterEvent.FirstNameChanged,
                is RegisterEvent.LastNameChanged,
                is RegisterEvent.PhoneNumberChanged,
                is RegisterEvent.StreetChanged,
                is RegisterEvent.HouseNumberChanged,
                is RegisterEvent.ApartmentNumberChanged,
                is RegisterEvent.CityChanged,
                is RegisterEvent.PostalCodeChanged,
                -> updateTextField(event)

                RegisterEvent.TogglePasswordVisibility,
                RegisterEvent.ToggleConfirmPasswordVisibility,
                -> updateOption(event)

                RegisterEvent.Submit -> submitRegister()
                RegisterEvent.NavigateToLogin -> _effects.trySend(RegisterEffect.NavigateToLogin)
            }
        }

        private fun updateTextField(event: RegisterEvent) {
            when (event) {
                is RegisterEvent.EmailChanged -> updateState { it.copy(email = event.value, errorMessage = null) }
                is RegisterEvent.PasswordChanged -> updateState { it.copy(password = event.value, errorMessage = null) }
                is RegisterEvent.ConfirmPasswordChanged ->
                    updateState { it.copy(confirmPassword = event.value, errorMessage = null) }

                is RegisterEvent.FirstNameChanged -> updateState { it.copy(firstName = event.value) }
                is RegisterEvent.LastNameChanged -> updateState { it.copy(lastName = event.value) }
                is RegisterEvent.PhoneNumberChanged -> updateState { it.copy(phoneNumber = event.value) }
                is RegisterEvent.StreetChanged -> updateState { it.copy(street = event.value) }
                is RegisterEvent.HouseNumberChanged -> updateState { it.copy(houseNumber = event.value) }
                is RegisterEvent.ApartmentNumberChanged -> updateState { it.copy(apartmentNumber = event.value) }
                is RegisterEvent.CityChanged -> updateState { it.copy(city = event.value) }
                is RegisterEvent.PostalCodeChanged -> updateState { it.copy(postalCode = event.value) }
                else -> Unit
            }
        }

        private fun updateOption(event: RegisterEvent) {
            when (event) {
                RegisterEvent.TogglePasswordVisibility ->
                    updateState { it.copy(passwordVisible = !it.passwordVisible) }

                RegisterEvent.ToggleConfirmPasswordVisibility ->
                    updateState { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }

                else -> Unit
            }
        }

        private fun submitRegister() {
            val current = _uiState.value
            if (!current.canSubmitLocal) return

            viewModelScope.launch {
                updateState { it.copy(isLoading = true, errorMessage = null) }

                runCatching { authRepository.register(current.toRegistrationDraft()) }
                    .onSuccess {
                        updateState { state -> state.copy(isLoading = false) }
                        _effects.trySend(RegisterEffect.NavigateToVerification(current.email))
                    }
                    .onFailure { error ->
                        updateState {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Server connection error",
                            )
                        }
                    }
            }
        }

        private fun updateState(transform: (RegisterUiState) -> RegisterUiState) {
            _uiState.update(transform)
        }
    }
