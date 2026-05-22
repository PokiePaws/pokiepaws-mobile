package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokiepaws.mobile.data.local.room.entities.OwnerProfileEntity

@Dao
interface OwnerProfileDao {
    @Query("SELECT * FROM owner_profile LIMIT 1")
    suspend fun getProfile(): OwnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: OwnerProfileEntity)

    @Query("UPDATE owner_profile SET phoneNumber = :phoneNumber")
    suspend fun updatePhoneNumber(phoneNumber: String)

    @Query(
        """
        UPDATE owner_profile
        SET street = :street,
            houseNumber = :houseNumber,
            apartmentNumber = :apartmentNumber,
            city = :city,
            postalCode = :postalCode,
            country = :country
        """,
    )
    suspend fun updateAddress(
        street: String,
        houseNumber: String,
        apartmentNumber: String?,
        city: String,
        postalCode: String,
        country: String,
    )

    @Query("DELETE FROM owner_profile")
    suspend fun clear()
}
