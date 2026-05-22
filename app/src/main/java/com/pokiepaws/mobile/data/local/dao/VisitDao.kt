package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokiepaws.mobile.data.local.room.entities.VisitEntity

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits WHERE scope = :scope ORDER BY startsAt DESC")
    suspend fun getVisits(scope: String): List<VisitEntity>

    @Query("SELECT * FROM visits WHERE id = :id LIMIT 1")
    suspend fun getVisit(id: Long): VisitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVisits(visits: List<VisitEntity>)

    @Query("DELETE FROM visits WHERE scope = :scope")
    suspend fun clearScope(scope: String)

    @Query("DELETE FROM visits")
    suspend fun clear()
}
