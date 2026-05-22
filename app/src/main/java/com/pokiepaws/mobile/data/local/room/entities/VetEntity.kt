package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vets")
data class VetEntity(
    @PrimaryKey val userId: Long,
    val clinicId: Long,
    val firstName: String,
    val lastName: String,
    val specialization: String?,
)
