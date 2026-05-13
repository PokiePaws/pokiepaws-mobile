package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.animal.AnimalRequest
import com.pokiepaws.mobile.data.remote.dto.animal.AnimalResponse
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import javax.inject.Inject

class AnimalRepositoryImpl
    @Inject
    constructor(
        private val apiService: AnimalApiService,
    ) : AnimalRepository {
        override suspend fun getAnimals(): List<Animal> {
            val response = apiService.getMyAnimals()
            return response.map { responseItem -> responseItem.toDomain() }
        }

        override suspend fun addAnimal(animal: AnimalDraft) {
            apiService.addAnimal(animal.toRequest())
        }

        override suspend fun deleteAnimal(id: Long) {
            apiService.deleteAnimal(id)
        }
    }

private fun AnimalResponse.toDomain(): Animal =
    Animal(
        id = id,
        name = name,
        species = species,
        breed = breed,
        weight = weight,
        birthDate = birthDate,
        notes = notes,
    )

private fun AnimalDraft.toRequest(): AnimalRequest =
    AnimalRequest(
        name = name,
        species = species,
        breed = breed,
        gender = gender,
        color = color,
        microchipNumber = microchipNumber,
        weight = weight,
        birthDate = birthDate,
        notes = notes,
    )
