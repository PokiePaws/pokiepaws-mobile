package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokiepaws.mobile.data.local.room.entities.ClinicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicDao {
    @Query("SELECT * FROM clinics ORDER BY clinicName COLLATE NOCASE")
    fun getClinics(): Flow<List<ClinicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClinics(clinics: List<ClinicEntity>)

    @Query("DELETE FROM clinics")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(clinics: List<ClinicEntity>) {
        clear()
        upsertClinics(clinics)
    }
}
