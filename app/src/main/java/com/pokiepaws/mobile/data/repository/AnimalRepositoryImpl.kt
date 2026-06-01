package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.room.mappers.toDomain
import com.pokiepaws.mobile.data.local.room.mappers.toEntity
import com.pokiepaws.mobile.data.remote.dto.animal.AnimalRequest
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.pokiepaws.mobile.data.remote.dto.animal.toDomain as responseToDomain

class AnimalRepositoryImpl
    @Inject
    constructor(
        private val apiService: AnimalApiService,
        private val animalDao: AnimalDao,
    ) : AnimalRepository {
        override fun getAnimals(): Flow<List<Animal>> {
            return animalDao.getAnimals().map { entities ->
                entities.map { it.toDomain() }
            }
        }

        override fun getAnimal(id: Long): Flow<Animal?> {
            return animalDao.getAnimal(id).map { it?.toDomain() }
        }

        override suspend fun syncAnimals() {
            runCatching {
                apiService.getMyAnimals().map { it.responseToDomain() }
            }.onSuccess { animals ->
                animalDao.replaceAll(animals.map { it.toEntity() })
            }
        }

        override suspend fun addAnimal(animal: AnimalDraft) {
            val addedAnimal = apiService.addAnimal(animal.toRequest()).responseToDomain()
            animalDao.upsertAnimals(listOf(addedAnimal.toEntity()))
        }

        override suspend fun deleteAnimal(id: Long) {
            apiService.deleteAnimal(id)
            syncAnimals()
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
