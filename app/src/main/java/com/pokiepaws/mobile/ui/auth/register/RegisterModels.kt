package com.pokiepaws.mobile.ui.auth.register

import com.pokiepaws.mobile.domain.model.RegistrationDraft
import com.pokiepaws.mobile.util.Countries
import com.pokiepaws.mobile.util.Country

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val phoneNumber: String = "",
    val phoneCountry: Country = Countries.first(),
    val residenceCountry: Country = Countries.find { it.name == "Polska" } ?: Countries.first(),
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
                emailValidationError == null &&
                passwordValidationErrors.isEmpty() &&
                passwordsMatch

    fun toRegistrationDraft(): RegistrationDraft =
        RegistrationDraft(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = "${phoneCountry.dialCode}$phoneNumber",
            street = street,
            houseNumber = houseNumber,
            apartmentNumber = apartmentNumber.ifBlank { null },
            city = city,
            postalCode = postalCode,
            country = residenceCountry.name,
        )
}

enum class EmailValidationError {
    InvalidFormat,
}

enum class PasswordValidationError {
    TooShort,
    MissingUppercase,
    MissingSpecialCharacter,
    ContainsUserName,
    ContainsSequence,
    ContainsRepeatedCharacters,
    TooCommon,
}

private val specialCharacterRegex = Regex("[^A-Za-z0-9]")
private val emailRegex = Regex("^[A-Za-z0-9+_.%\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
private val repeatedCharacterRegex = Regex("(.)\\1{2,}")
private val keyboardSequences =
    listOf(
        "abcdefghijklmnopqrstuvwxyz",
        "qwertyuiop",
        "asdfghjkl",
        "zxcvbnm",
        "0123456789",
    )
private val commonPasswords =
    setOf(
        "123456",
        "123456789",
        "qwerty",
        "password",
        "1234567",
        "12345678",
        "12345",
        "iloveyou",
        "111111",
        "123123",
        "abc123",
        "qwerty123",
        "1q2w3e4r",
        "admin",
        "qwertyuiop",
        "654321",
        "555555",
        "lovely",
        "7777777",
        "welcome",
        "888888",
        "princess",
        "dragon",
        "password1",
        "123qwe",
        "wpuscmnie",
    )

private fun validateEmail(email: String): EmailValidationError? {
    if (email.isBlank()) return null
    return if (emailRegex.matches(email.trim())) null else EmailValidationError.InvalidFormat
}

private fun validatePassword(
    password: String,
    email: String,
    firstName: String,
    lastName: String,
): List<PasswordValidationError> {
    if (password.isBlank()) return emptyList()

    val normalizedPassword = password.lowercase()
    return buildList {
        if (password.length < MIN_PASSWORD_LENGTH) add(PasswordValidationError.TooShort)
        if (password.none { it.isUpperCase() }) add(PasswordValidationError.MissingUppercase)
        if (!specialCharacterRegex.containsMatchIn(password)) add(PasswordValidationError.MissingSpecialCharacter)
        if (containsUserNamePart(normalizedPassword, email, firstName, lastName)) {
            add(PasswordValidationError.ContainsUserName)
        }
        if (containsSequence(normalizedPassword)) add(PasswordValidationError.ContainsSequence)
        if (repeatedCharacterRegex.containsMatchIn(normalizedPassword)) {
            add(PasswordValidationError.ContainsRepeatedCharacters)
        }
        if (normalizedPassword in commonPasswords) add(PasswordValidationError.TooCommon)
    }
}

private fun containsUserNamePart(
    normalizedPassword: String,
    email: String,
    firstName: String,
    lastName: String,
): Boolean {
    val userParts =
        listOf(
            email.substringBefore("@").lowercase(),
            firstName.lowercase(),
            lastName.lowercase(),
        ).filter { it.length >= MIN_USER_PART_LENGTH }

    return userParts.any { part -> normalizedPassword == part || normalizedPassword.contains(part) }
}

private fun containsSequence(normalizedPassword: String): Boolean {
    return keyboardSequences.any { sequence ->
        sequence.windowed(SEQUENCE_LENGTH).any { part ->
            normalizedPassword.contains(part) || normalizedPassword.contains(part.reversed())
        }
    }
}

private const val MIN_PASSWORD_LENGTH = 12
private const val MIN_USER_PART_LENGTH = 3
private const val SEQUENCE_LENGTH = 3

sealed interface RegisterEvent {
    data class EmailChanged(val value: String) : RegisterEvent

    data class PasswordChanged(val value: String) : RegisterEvent

    data class ConfirmPasswordChanged(val value: String) : RegisterEvent

    data class FirstNameChanged(val value: String) : RegisterEvent

    data class LastNameChanged(val value: String) : RegisterEvent

    data object TogglePasswordVisibility : RegisterEvent

    data object ToggleConfirmPasswordVisibility : RegisterEvent

    data class PhoneNumberChanged(val value: String) : RegisterEvent

    data class PhoneCountryChanged(val value: Country) : RegisterEvent

    data class ResidenceCountryChanged(val value: Country) : RegisterEvent

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
