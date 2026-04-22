package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.AnimalApiService
import com.pokiepaws.mobile.data.remote.AnimalRequest
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import javax.inject.Inject

class AnimalRepositoryImpl
    @Inject
    constructor(
        private val apiService: AnimalApiService,
    ) : AnimalRepository {
        override suspend fun getAnimals(): List<Animal> {
            // Pobieramy dane z API
            val response = apiService.getMyAnimals()

            // Mapujemy AnimalResponse na czysty model Animal z Domain
            return response.map { responseItem ->
                Animal(
                    id = responseItem.id,
                    name = responseItem.name,
                    species = responseItem.species,
                    breed = responseItem.breed,
                    weight = responseItem.weight,
                    birthDate = responseItem.birthDate,
                    notes = responseItem.notes,
                )
            }
        }

        override suspend fun addAnimal(request: AnimalRequest): Result<Unit> {
            return try {
                apiService.addAnimal(request)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun deleteAnimal(id: Long): Result<Unit> {
            return try {
                apiService.deleteAnimal(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
