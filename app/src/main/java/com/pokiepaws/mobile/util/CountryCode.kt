package com.pokiepaws.mobile.util

data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String,
)

val popularCountries =
    listOf(
        Country("Polska", "PL", "+48", "🇵🇱"),
        Country("Niemcy", "DE", "+49", "🇩🇪"),
        Country("Wielka Brytania", "GB", "+44", "🇬🇧"),
        Country("USA", "US", "+1", "🇺🇸"),
        Country("Francja", "FR", "+33", "🇫🇷"),
        Country("Włochy", "IT", "+39", "🇮🇹"),
        Country("Hiszpania", "ES", "+34", "🇪🇸"),
        Country("Holandia", "NL", "+31", "🇳🇱"),
        Country("Belgia", "BE", "+32", "🇧🇪"),
        Country("Austria", "AT", "+43", "🇦🇹"),
        Country("Szwajcaria", "CH", "+41", "🇨🇭"),
        Country("Czechy", "CZ", "+420", "🇨🇿"),
        Country("Słowacja", "SK", "+421", "🇸🇰"),
        Country("Ukraina", "UA", "+380", "🇺🇦"),
        Country("Norwegia", "NO", "+47", "🇳🇴"),
        Country("Szwecja", "SE", "+46", "🇸🇪"),
        Country("Dania", "DK", "+45", "🇩🇰"),
        Country("Finlandia", "FI", "+358", "🇫🇮"),
    )
