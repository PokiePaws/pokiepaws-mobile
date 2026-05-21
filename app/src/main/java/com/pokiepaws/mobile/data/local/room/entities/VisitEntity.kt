package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity

@Entity(tableName = "visits", primaryKeys = ["scope", "id"])
data class VisitEntity(
    val scope: String,
    val id: Long,
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val endsAt: String,
    val description: String,
    val disease: String?,
    val diagnosis: String?,
    val recommendations: String?,
    val status: String,
)
