package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prescriptions",
    indices = [Index(value = ["visitId"], unique = true)],
)
data class PrescriptionEntity(
    @PrimaryKey val id: Long,
    val visitId: Long,
    val vetUserId: Long,
    val clinicId: Long,
    val recommendationDate: String?,
    val creationDate: String?,
)
