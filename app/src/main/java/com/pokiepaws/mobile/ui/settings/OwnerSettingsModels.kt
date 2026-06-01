package com.pokiepaws.mobile.ui.settings

import com.pokiepaws.mobile.domain.model.OwnerAddressDraft
import com.pokiepaws.mobile.domain.model.OwnerPhoneDraft
import com.pokiepaws.mobile.domain.validation.PasswordValidationError
import com.pokiepaws.mobile.domain.validation.PostalCodeValidationError
import com.pokiepaws.mobile.domain.validation.validatePassword
import com.pokiepaws.mobile.domain.validation.validatePostalCode

data class OwnerSettingsUiState(
    val phoneNumber: String = "",
    val street: String = "",
    val houseNumber: String = "",
    val apartmentNumber: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val currentPasswordVisible: Boolean = false,
    val newPasswordVisible: Boolean = false,
    val confirmNewPasswordVisible: Boolean = false,
    val isSavingPhone: Boolean = false,
    val isSavingAddress: Boolean = false,
    val isChangingPassword: Boolean = false,
    val isOnline: Boolean = true,
    val phoneSaved: Boolean = false,
    val addressSaved: Boolean = false,
    val passwordChanged: Boolean = false,
    val foreignTravelPlanned: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deleteAccountError: String? = null,
    val errorMessage: String? = null,
) {
    val passwordValidationErrors: List<PasswordValidationError>
        get() =
            validatePassword(
                password = newPassword,
                email = "",
                firstName = "",
                lastName = "",
            )

    val passwordsMatch: Boolean get() = newPassword == confirmNewPassword || confirmNewPassword.isBlank()
    val postalCodeValidationError: PostalCodeValidationError? get() = validatePostalCode(postalCode)

    val canSavePhone: Boolean get() = phoneNumber.isNotBlank() && isOnline

    val canSaveAddress: Boolean
        get() =
            street.isNotBlank() &&
                houseNumber.isNotBlank() &&
                city.isNotBlank() &&
                postalCode.isNotBlank() &&
                postalCodeValidationError == null &&
                country.isNotBlank() &&
                isOnline

    val canChangePassword: Boolean
        get() =
            currentPassword.isNotBlank() &&
                newPassword.isNotBlank() &&
                confirmNewPassword.isNotBlank() &&
                passwordValidationErrors.isEmpty() &&
                passwordsMatch &&
                isOnline

    fun toOwnerPhoneDraft(): OwnerPhoneDraft =
        OwnerPhoneDraft(
            phoneNumber = phoneNumber,
        )

    fun toOwnerAddressDraft(): OwnerAddressDraft =
        OwnerAddressDraft(
            street = street,
            houseNumber = houseNumber,
            apartmentNumber = apartmentNumber.ifBlank { null },
            city = city,
            postalCode = postalCode,
            country = country,
        )
}

sealed interface OwnerSettingsEvent {
    data class PhoneNumberChanged(val value: String) : OwnerSettingsEvent

    data class StreetChanged(val value: String) : OwnerSettingsEvent

    data class HouseNumberChanged(val value: String) : OwnerSettingsEvent

    data class ApartmentNumberChanged(val value: String) : OwnerSettingsEvent

    data class CityChanged(val value: String) : OwnerSettingsEvent

    data class PostalCodeChanged(val value: String) : OwnerSettingsEvent

    data class CountryChanged(val value: String) : OwnerSettingsEvent

    data class CurrentPasswordChanged(val value: String) : OwnerSettingsEvent

    data class NewPasswordChanged(val value: String) : OwnerSettingsEvent

    data class ConfirmNewPasswordChanged(val value: String) : OwnerSettingsEvent

    data class ForeignTravelPlannedChanged(val value: Boolean) : OwnerSettingsEvent

    data object ToggleCurrentPasswordVisibility : OwnerSettingsEvent

    data object ToggleNewPasswordVisibility : OwnerSettingsEvent

    data object ToggleConfirmNewPasswordVisibility : OwnerSettingsEvent

    data object SavePhone : OwnerSettingsEvent

    data object SaveAddress : OwnerSettingsEvent

    data object ChangePassword : OwnerSettingsEvent

    data object ClearDeleteAccountError : OwnerSettingsEvent
}
