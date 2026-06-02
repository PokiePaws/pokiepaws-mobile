package com.pokiepaws.mobile.data.local.room.entities

import androidx.room.Entity

@Entity(tableName = "prescription_items", primaryKeys = ["prescriptionId", "id"])
data class PrescriptionItemEntity(
    val prescriptionId: Long,
    val id: Long,
    val productId: Long,
    val productName: String?,
    val quantityPackages: Int?,
    val dosage: String?,
    val treatmentTime: String?,
)
