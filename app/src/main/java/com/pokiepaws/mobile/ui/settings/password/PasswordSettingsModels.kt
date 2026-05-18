package com.pokiepaws.mobile.ui.settings.password

import com.pokiepaws.mobile.domain.validation.PasswordValidationError
import com.pokiepaws.mobile.ui.settings.OwnerSettingsUiState

data class PasswordSettingsUiState(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
    val currentPasswordVisible: Boolean,
    val newPasswordVisible: Boolean,
    val confirmNewPasswordVisible: Boolean,
    val validationErrors: List<PasswordValidationError>,
    val passwordsMatch: Boolean,
    val isChanging: Boolean,
    val changed: Boolean,
    val canChange: Boolean,
) {
    companion object {
        fun from(state: OwnerSettingsUiState): PasswordSettingsUiState =
            PasswordSettingsUiState(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword,
                confirmNewPassword = state.confirmNewPassword,
                currentPasswordVisible = state.currentPasswordVisible,
                newPasswordVisible = state.newPasswordVisible,
                confirmNewPasswordVisible = state.confirmNewPasswordVisible,
                validationErrors = state.passwordValidationErrors,
                passwordsMatch = state.passwordsMatch,
                isChanging = state.isChangingPassword,
                changed = state.passwordChanged,
                canChange = state.canChangePassword,
            )
    }
}
