package com.pokiepaws.mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import com.pokiepaws.mobile.domain.model.OwnerProfile
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
import com.pokiepaws.mobile.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerSettingsViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val appSettingsRepository: AppSettingsRepository,
        private val connectivityObserver: ConnectivityObserver,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(OwnerSettingsUiState(isOnline = connectivityObserver.isOnline.value))
        val uiState: StateFlow<OwnerSettingsUiState> = _uiState.asStateFlow()

        init {
            observeStateChanges()
            syncOwnerProfile()
        }

        private fun observeStateChanges() {
            viewModelScope.launch {
                connectivityObserver.isOnline.collect { online ->
                    updateState { it.copy(isOnline = online) }
                }
            }
            viewModelScope.launch {
                authRepository.observeProfile().collect { profile ->
                    profile?.let { p ->
                        updateState { current -> current.withOwnerProfile(p) }
                    }
                }
            }
            viewModelScope.launch {
                appSettingsRepository.foreignTravelPlanned.collect { value ->
                    updateState { it.copy(foreignTravelPlanned = value) }
                }
            }
        }

        private fun syncOwnerProfile() {
            viewModelScope.launch {
                updateState { it.copy(errorMessage = null) }
                runCatching { authRepository.syncProfile() }
                    .onFailure { error ->
                        updateState {
                            it.copy(errorMessage = error.message ?: "Server connection error")
                        }
                    }
            }
        }

        fun onEvent(event: OwnerSettingsEvent) {
            when (event) {
                is OwnerSettingsEvent.PhoneNumberChanged,
                is OwnerSettingsEvent.StreetChanged,
                is OwnerSettingsEvent.HouseNumberChanged,
                is OwnerSettingsEvent.ApartmentNumberChanged,
                is OwnerSettingsEvent.CityChanged,
                is OwnerSettingsEvent.PostalCodeChanged,
                is OwnerSettingsEvent.CountryChanged,
                is OwnerSettingsEvent.CurrentPasswordChanged,
                is OwnerSettingsEvent.NewPasswordChanged,
                is OwnerSettingsEvent.ConfirmNewPasswordChanged,
                -> updateTextField(event)
                is OwnerSettingsEvent.ToggleCurrentPasswordVisibility,
                is OwnerSettingsEvent.ToggleNewPasswordVisibility,
                is OwnerSettingsEvent.ToggleConfirmNewPasswordVisibility,
                -> updatePasswordVisibility(event)
                OwnerSettingsEvent.SavePhone -> savePhone()
                OwnerSettingsEvent.SaveAddress -> saveAddress()
                OwnerSettingsEvent.ChangePassword -> changePassword()
                OwnerSettingsEvent.ClearDeleteAccountError -> clearDeleteAccountError()
                is OwnerSettingsEvent.ForeignTravelPlannedChanged -> setForeignTravelPlanned(event.value)
            }
        }

        fun deleteAccount(onDeleted: () -> Unit) {
            if (!connectivityObserver.isOnline.value) {
                updateState { it.copy(deleteAccountError = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }

            viewModelScope.launch {
                updateState {
                    it.copy(
                        isDeletingAccount = true,
                        deleteAccountError = null,
                    )
                }
                runCatching { authRepository.deleteAccount() }
                    .onSuccess { onDeleted() }
                    .onFailure { error ->
                        updateState {
                            it.copy(
                                isDeletingAccount = false,
                                deleteAccountError = error.message ?: "Could not delete account",
                            )
                        }
                    }
            }
        }

        private fun updateTextField(event: OwnerSettingsEvent) {
            when (event) {
                is OwnerSettingsEvent.PhoneNumberChanged -> updateState { it.copy(phoneNumber = event.value) }
                is OwnerSettingsEvent.StreetChanged -> updateState { it.copy(street = event.value) }
                is OwnerSettingsEvent.HouseNumberChanged -> updateState { it.copy(houseNumber = event.value) }
                is OwnerSettingsEvent.ApartmentNumberChanged -> updateState { it.copy(apartmentNumber = event.value) }
                is OwnerSettingsEvent.CityChanged -> updateState { it.copy(city = event.value) }
                is OwnerSettingsEvent.PostalCodeChanged -> updateState { it.copy(postalCode = event.value) }
                is OwnerSettingsEvent.CountryChanged -> updateState { it.copy(country = event.value) }
                is OwnerSettingsEvent.CurrentPasswordChanged -> updateState { it.copy(currentPassword = event.value) }
                is OwnerSettingsEvent.NewPasswordChanged -> updateState { it.copy(newPassword = event.value) }
                is OwnerSettingsEvent.ConfirmNewPasswordChanged ->
                    updateState { it.copy(confirmNewPassword = event.value) }

                else -> Unit
            }
        }

        private fun updatePasswordVisibility(event: OwnerSettingsEvent) {
            when (event) {
                OwnerSettingsEvent.ToggleCurrentPasswordVisibility ->
                    updateState { it.copy(currentPasswordVisible = !it.currentPasswordVisible) }

                OwnerSettingsEvent.ToggleNewPasswordVisibility ->
                    updateState { it.copy(newPasswordVisible = !it.newPasswordVisible) }

                OwnerSettingsEvent.ToggleConfirmNewPasswordVisibility ->
                    updateState { it.copy(confirmNewPasswordVisible = !it.confirmNewPasswordVisible) }

                else -> Unit
            }
        }

        private fun savePhone() {
            val current = _uiState.value
            if (!connectivityObserver.isOnline.value) {
                updateState { it.copy(errorMessage = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }
            if (!current.canSavePhone) return

            viewModelScope.launch {
                updateState { it.copy(isSavingPhone = true, phoneSaved = false, errorMessage = null) }
                runCatching { authRepository.updateOwnerPhone(current.toOwnerPhoneDraft()) }
                    .onSuccess {
                        updateState { it.copy(isSavingPhone = false, phoneSaved = true) }
                    }
                    .onFailure { error ->
                        updateState {
                            it.copy(
                                isSavingPhone = false,
                                errorMessage = error.message ?: "Server connection error",
                            )
                        }
                    }
            }
        }

        private fun saveAddress() {
            val current = _uiState.value
            if (!connectivityObserver.isOnline.value) {
                updateState { it.copy(errorMessage = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }
            if (!current.canSaveAddress) return

            viewModelScope.launch {
                updateState { it.copy(isSavingAddress = true, addressSaved = false, errorMessage = null) }
                runCatching { authRepository.updateOwnerAddress(current.toOwnerAddressDraft()) }
                    .onSuccess {
                        updateState { it.copy(isSavingAddress = false, addressSaved = true) }
                    }
                    .onFailure { error ->
                        updateState {
                            it.copy(
                                isSavingAddress = false,
                                errorMessage = error.message ?: "Server connection error",
                            )
                        }
                    }
            }
        }

        private fun changePassword() {
            val current = _uiState.value
            if (!connectivityObserver.isOnline.value) {
                updateState { it.copy(errorMessage = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }
            if (!current.canChangePassword) return

            viewModelScope.launch {
                updateState { it.copy(isChangingPassword = true, passwordChanged = false, errorMessage = null) }
                runCatching {
                    authRepository.changePassword(
                        currentPassword = current.currentPassword,
                        newPassword = current.newPassword,
                    )
                }.onSuccess {
                    updateState {
                        it.copy(
                            currentPassword = "",
                            newPassword = "",
                            confirmNewPassword = "",
                            isChangingPassword = false,
                            passwordChanged = true,
                        )
                    }
                }.onFailure { error ->
                    updateState {
                        it.copy(
                            isChangingPassword = false,
                            errorMessage = error.message ?: "Server connection error",
                        )
                    }
                }
            }
        }

        private fun setForeignTravelPlanned(value: Boolean) {
            if (!connectivityObserver.isOnline.value) {
                updateState { it.copy(errorMessage = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }

            viewModelScope.launch {
                appSettingsRepository.setForeignTravelPlanned(value)
            }
        }

        private fun clearDeleteAccountError() {
            updateState { it.copy(deleteAccountError = null) }
        }

        private fun updateState(transform: (OwnerSettingsUiState) -> OwnerSettingsUiState) {
            _uiState.update(transform)
        }
    }

private const val ONLINE_ACTION_REQUIRED_MESSAGE = "Ta akcja wymaga połączenia z internetem"

private fun OwnerSettingsUiState.withOwnerProfile(profile: OwnerProfile): OwnerSettingsUiState =
    copy(
        phoneNumber = profile.phoneNumber,
        street = profile.street,
        houseNumber = profile.houseNumber,
        apartmentNumber = profile.apartmentNumber.orEmpty(),
        city = profile.city,
        postalCode = profile.postalCode,
        country = profile.country,
    )
