package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.AnimalEntity
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalSpecies

fun AnimalEntity.toDomain(): Animal =
    Animal(
        id = id,
        name = name,
        species = AnimalSpecies.fromApiValue(species),
        breed = breed,
        gender = gender,
        color = color,
        microchipNumber = microchipNumber,
        weight = weight,
        birthDate = birthDate,
        nextRabiesVaccinationDate = nextRabiesVaccinationDate,
        notes = notes,
    )

fun Animal.toEntity(): AnimalEntity =
    AnimalEntity(
        id = id,
        name = name,
        species = species.apiValue,
        breed = breed,
        gender = gender,
        color = color,
        microchipNumber = microchipNumber,
        weight = weight,
        birthDate = birthDate,
        nextRabiesVaccinationDate = nextRabiesVaccinationDate,
        notes = notes,
    )
