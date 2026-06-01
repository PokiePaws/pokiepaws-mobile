package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokiepaws.mobile.data.local.room.entities.VetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VetDao {
    @Query("SELECT * FROM vets WHERE clinicId = :clinicId ORDER BY lastName COLLATE NOCASE, firstName COLLATE NOCASE")
    fun getByClinic(clinicId: Long): Flow<List<VetEntity>>

    @Query("DELETE FROM vets WHERE clinicId = :clinicId")
    suspend fun clearClinic(clinicId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVets(vets: List<VetEntity>)

    @Query("DELETE FROM vets")
    suspend fun clear()

    @Transaction
    suspend fun replaceClinic(
        clinicId: Long,
        vets: List<VetEntity>,
    ) {
        clearClinic(clinicId)
        upsertVets(vets)
    }
}
