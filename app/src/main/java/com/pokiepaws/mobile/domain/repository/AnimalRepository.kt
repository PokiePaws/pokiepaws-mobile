package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.data.remote.AnimalRequest
import com.pokiepaws.mobile.domain.model.Animal

interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>

    suspend fun addAnimal(request: AnimalRequest): Result<Unit>

    suspend fun deleteAnimal(id: Long): Result<Unit>
}
