package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokiepaws.mobile.data.local.room.entities.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY name COLLATE NOCASE")
    fun getAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    fun getAnimal(id: Long): Flow<AnimalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnimals(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(animals: List<AnimalEntity>) {
        clear()
        upsertAnimals(animals)
    }
}
