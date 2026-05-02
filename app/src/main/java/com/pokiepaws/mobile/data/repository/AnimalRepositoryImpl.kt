package com.pokiepaws.mobile.data.repository

import com.pokiepaws.mobile.data.remote.dto.animal.AnimalRequest
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AnimalRepositoryImpl
    @Inject
    constructor(
        private val apiService: AnimalApiService,
    ) : AnimalRepository {
        override suspend fun getAnimals(): List<Animal> {
            val response = apiService.getMyAnimals()
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
            } catch (e: HttpException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            }
        }

        override suspend fun deleteAnimal(id: Long): Result<Unit> {
            return try {
                apiService.deleteAnimal(id)
                Result.success(Unit)
            } catch (e: HttpException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            }
        }
    }
