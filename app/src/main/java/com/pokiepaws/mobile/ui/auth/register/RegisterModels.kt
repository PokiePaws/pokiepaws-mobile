package com.pokiepaws.mobile.ui.auth.register

import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.domain.validation.EmailValidationError
import com.pokiepaws.mobile.domain.validation.PasswordValidationError
import com.pokiepaws.mobile.domain.validation.PostalCodeValidationError
import com.pokiepaws.mobile.domain.validation.validateEmail
import com.pokiepaws.mobile.domain.validation.validatePassword
import com.pokiepaws.mobile.domain.validation.validatePostalCode

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val phoneNumber: String = "",
    val street: String = "",
    val houseNumber: String = "",
    val apartmentNumber: String = "",
    val city: String = "",
    val postalCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val passwordsMatch: Boolean get() = password == confirmPassword || confirmPassword.isEmpty()
    val emailValidationError: EmailValidationError? get() = validateEmail(email)
    val postalCodeValidationError: PostalCodeValidationError? get() = validatePostalCode(postalCode)
    val passwordValidationErrors: List<PasswordValidationError>
        get() =
            validatePassword(
                password = password,
                email = email,
                firstName = firstName,
                lastName = lastName,
            )
    val canSubmitLocal: Boolean
        get() =
            email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                postalCodeValidationError == null &&
                emailValidationError == null &&
                passwordValidationErrors.isEmpty() &&
                passwordsMatch

    fun toRegistrationDraft(): RegistrationDraft =
        RegistrationDraft(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = "$POLAND_DIAL_CODE$phoneNumber",
            street = street,
            houseNumber = houseNumber,
            apartmentNumber = apartmentNumber.ifBlank { null },
            city = city,
            postalCode = postalCode,
            country = POLAND_COUNTRY_NAME,
        )
}

sealed interface RegisterEvent {
    data class EmailChanged(val value: String) : RegisterEvent

    data class PasswordChanged(val value: String) : RegisterEvent

    data class ConfirmPasswordChanged(val value: String) : RegisterEvent

    data class FirstNameChanged(val value: String) : RegisterEvent

    data class LastNameChanged(val value: String) : RegisterEvent

    data object TogglePasswordVisibility : RegisterEvent

    data object ToggleConfirmPasswordVisibility : RegisterEvent

    data class PhoneNumberChanged(val value: String) : RegisterEvent

    data class StreetChanged(val value: String) : RegisterEvent

    data class HouseNumberChanged(val value: String) : RegisterEvent

    data class ApartmentNumberChanged(val value: String) : RegisterEvent

    data class CityChanged(val value: String) : RegisterEvent

    data class PostalCodeChanged(val value: String) : RegisterEvent

    data object Submit : RegisterEvent

    data object NavigateToLogin : RegisterEvent
}

sealed interface RegisterEffect {
    data class NavigateToVerification(val email: String) : RegisterEffect

    data object NavigateToLogin : RegisterEffect
}

internal const val POLAND_COUNTRY_NAME = "Polska"
internal const val POLAND_DIAL_CODE = "+48"
