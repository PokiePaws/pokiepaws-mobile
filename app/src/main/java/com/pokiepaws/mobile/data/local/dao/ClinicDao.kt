package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokiepaws.mobile.data.local.room.entities.ClinicEntity

@Dao
interface ClinicDao {
    @Query("SELECT * FROM clinics ORDER BY clinicName COLLATE NOCASE")
    suspend fun getClinics(): List<ClinicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClinics(clinics: List<ClinicEntity>)

    @Query("DELETE FROM clinics")
    suspend fun clear()
}
