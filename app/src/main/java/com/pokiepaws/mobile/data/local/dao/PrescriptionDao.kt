package com.pokiepaws.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokiepaws.mobile.data.local.room.entities.PrescriptionEntity
import com.pokiepaws.mobile.data.local.room.entities.PrescriptionItemEntity
import com.pokiepaws.mobile.data.local.room.relations.PrescriptionWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {
    @Transaction
    @Query("SELECT * FROM prescriptions WHERE visitId = :visitId LIMIT 1")
    fun getByVisit(visitId: Long): Flow<PrescriptionWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrescription(prescription: PrescriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItems(items: List<PrescriptionItemEntity>)

    @Query("SELECT id FROM prescriptions WHERE visitId = :visitId")
    suspend fun getPrescriptionIdsForVisit(visitId: Long): List<Long>

    @Query("DELETE FROM prescription_items WHERE prescriptionId IN (:prescriptionIds)")
    suspend fun deleteItemsForPrescriptions(prescriptionIds: List<Long>)

    @Query("DELETE FROM prescriptions WHERE visitId = :visitId")
    suspend fun deletePrescriptionForVisit(visitId: Long)

    @Query("DELETE FROM prescription_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM prescriptions")
    suspend fun deleteAllPrescriptions()

    @Transaction
    suspend fun replaceForVisit(
        visitId: Long,
        prescription: PrescriptionEntity?,
        items: List<PrescriptionItemEntity>,
    ) {
        clearForVisit(visitId)
        if (prescription != null) {
            upsertPrescription(prescription)
            upsertItems(items)
        }
    }

    @Transaction
    suspend fun clearForVisit(visitId: Long) {
        val prescriptionIds = getPrescriptionIdsForVisit(visitId)
        if (prescriptionIds.isNotEmpty()) {
            deleteItemsForPrescriptions(prescriptionIds)
        }
        deletePrescriptionForVisit(visitId)
    }

    @Transaction
    suspend fun clear() {
        deleteAllItems()
        deleteAllPrescriptions()
    }
}
