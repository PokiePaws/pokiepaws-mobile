package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokiepaws.mobile.data.local.room.entities.VisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits WHERE scope = :scope ORDER BY startsAt DESC")
    fun getVisits(scope: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE id = :id LIMIT 1")
    fun getVisit(id: Long): Flow<VisitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVisits(visits: List<VisitEntity>)

    @Query("DELETE FROM visits WHERE scope = :scope")
    suspend fun clearScope(scope: String)

    @Query("DELETE FROM visits")
    suspend fun clear()

    @Transaction
    suspend fun replaceScope(
        scope: String,
        visits: List<VisitEntity>,
    ) {
        clearScope(scope)
        upsertVisits(visits)
    }
}
