package com.pokiepaws.mobile.domain.model

data class AnimalDraft(
    val name: String,
    val species: String,
    val breed: String? = null,
    val gender: String,
    val color: String? = null,
    val microchipNumber: String? = null,
    val weight: Double? = null,
    val birthDate: String? = null,
    val notes: String? = null,
)
