package com.pokiepaws.mobile.domain.model

data class Animal(
    val id: Long,
    val name: String,
    val species: AnimalSpecies,
    val breed: String? = null,
    val gender: String? = null,
    val color: String? = null,
    val microchipNumber: String? = null,
    val weight: Double? = null,
    val birthDate: String? = null,
    val nextRabiesVaccinationDate: String? = null,
    val notes: String? = null,
)
