package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokiepaws.mobile.data.local.room.entities.VetEntity

@Dao
interface VetDao {
    @Query("SELECT * FROM vets WHERE clinicId = :clinicId ORDER BY lastName COLLATE NOCASE, firstName COLLATE NOCASE")
    suspend fun getByClinic(clinicId: Long): List<VetEntity>

    @Query("DELETE FROM vets WHERE clinicId = :clinicId")
    suspend fun clearClinic(clinicId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVets(vets: List<VetEntity>)

    @Query("DELETE FROM vets")
    suspend fun clear()
}
