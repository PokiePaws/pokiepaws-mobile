package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_profile")
data class OwnerProfileEntity(
    @PrimaryKey val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?,
    val postalCode: String,
    val city: String,
    val country: String,
)
