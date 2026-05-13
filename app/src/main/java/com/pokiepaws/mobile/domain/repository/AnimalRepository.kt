package com.pokiepaws.mobile.domain.repository

import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft

interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>

    suspend fun addAnimal(animal: AnimalDraft)

    suspend fun deleteAnimal(id: Long)
}
