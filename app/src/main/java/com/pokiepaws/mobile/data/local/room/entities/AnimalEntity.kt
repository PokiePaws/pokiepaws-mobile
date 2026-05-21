package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val species: String,
    val breed: String?,
    val gender: String?,
    val color: String?,
    val microchipNumber: String?,
    val weight: Double?,
    val birthDate: String?,
    val nextRabiesVaccinationDate: String?,
    val notes: String?,
)
