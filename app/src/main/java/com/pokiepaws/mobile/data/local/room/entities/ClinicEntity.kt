package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinics")
data class ClinicEntity(
    @PrimaryKey val id: Long,
    val clinicName: String,
    val city: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val country: String,
    val phone: String?,
    val email: String?,
)
