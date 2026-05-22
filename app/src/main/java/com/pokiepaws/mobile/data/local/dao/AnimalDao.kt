package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokiepaws.mobile.data.local.room.entities.AnimalEntity

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY name COLLATE NOCASE")
    suspend fun getAnimals(): List<AnimalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnimals(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals")
    suspend fun clear()
}
