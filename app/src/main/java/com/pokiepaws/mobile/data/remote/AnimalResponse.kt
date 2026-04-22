package com.pokiepaws.mobile.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AnimalResponse(
    val id: Long,
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

// Funkcja rozszerzająca (Mapper), która zamienia model API na model domenowy
fun AnimalResponse.toDomain(): com.pokiepaws.mobile.domain.model.Animal {
    return com.pokiepaws.mobile.domain.model.Animal(
        id = this.id,
        name = this.name,
        species = this.species,
        breed = this.breed,
        weight = this.weight,
        birthDate = this.birthDate,
        notes = this.notes,
    )
}
