package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.animal.AnimalRequest
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import java.io.IOException
import javax.inject.Inject
import com.pokiepaws.mobile.data.remote.dto.animal.toDomain as responseToDomain

class AnimalRepositoryImpl
    @Inject
    constructor(
        private val apiService: AnimalApiService,
        private val animalDao: AnimalDao,
    ) : AnimalRepository {
        override suspend fun getAnimals(): List<Animal> {
            return runCatching {
                apiService.getMyAnimals().map { it.responseToDomain() }
            }.onSuccess { animals ->
                animalDao.clear()
                animalDao.upsertAnimals(animals.map { it.toEntity() })
            }.getOrElse { error ->
                val cached = animalDao.getAnimals().map { it.toDomain() }
                if (cached.isNotEmpty() && error is IOException) cached else throw error
            }
        }

        override suspend fun addAnimal(animal: AnimalDraft) {
            val addedAnimal = apiService.addAnimal(animal.toRequest()).responseToDomain()
            animalDao.upsertAnimals(listOf(addedAnimal.toEntity()))
        }

        override suspend fun deleteAnimal(id: Long) {
            apiService.deleteAnimal(id)
            animalDao.clear()
            animalDao.upsertAnimals(apiService.getMyAnimals().map { it.responseToDomain().toEntity() })
        }
    }

private fun AnimalDraft.toRequest(): AnimalRequest =
    AnimalRequest(
        name = name,
        species = species.apiValue,
        breed = breed,
        gender = gender,
        color = color,
        microchipNumber = microchipNumber,
        weight = weight,
        birthDate = birthDate,
        notes = notes,
    )
