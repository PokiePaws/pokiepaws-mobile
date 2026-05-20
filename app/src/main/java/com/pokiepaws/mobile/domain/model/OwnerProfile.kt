package com.pokiepaws.mobile.domain.model

data class OwnerProfile(
    val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val postalCode: String,
    val city: String,
    val country: String,
) {
    val displayName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    val initials: String
        get() =
            listOf(firstName, lastName)
                .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
                .joinToString("")
                .ifBlank { email.firstOrNull()?.uppercaseChar()?.toString().orEmpty() }
}
