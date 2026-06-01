package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimals(): Flow<List<Animal>>

    fun getAnimal(id: Long): Flow<Animal?>

    suspend fun syncAnimals()

    suspend fun addAnimal(animal: AnimalDraft)

    suspend fun deleteAnimal(id: Long)
}
