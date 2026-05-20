package com.pokiepaws.mobile.domain.validation

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

enum class PostalCodeValidationError {
    InvalidFormat,
}

private val specialCharacterRegex = Regex("[^A-Za-z0-9]")
private val emailRegex = Regex("^[A-Za-z0-9+_.%\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
private val polishPostalCodeRegex = Regex("^\\d{2}-\\d{3}$")
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

fun validateEmail(email: String): EmailValidationError? {
    if (email.isBlank()) return null
    return if (emailRegex.matches(email.trim())) null else EmailValidationError.InvalidFormat
}

fun validatePostalCode(postalCode: String): PostalCodeValidationError? {
    if (postalCode.isBlank()) return null
    return if (polishPostalCodeRegex.matches(postalCode.trim())) null else PostalCodeValidationError.InvalidFormat
}

fun validatePassword(
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
