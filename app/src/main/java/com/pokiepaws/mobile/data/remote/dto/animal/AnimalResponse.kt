package com.pokiepaws.mobile.data.remote.dto.animal

import com.pokiepaws.mobile.domain.model.Animal
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

fun AnimalResponse.toDomain(): Animal {
    return Animal(
        id = this.id,
        name = this.name,
        species = this.species,
        breed = this.breed,
        gender = this.gender,
        color = this.color,
        microchipNumber = this.microchipNumber,
        weight = this.weight,
        birthDate = this.birthDate,
        notes = this.notes,
    )
}
