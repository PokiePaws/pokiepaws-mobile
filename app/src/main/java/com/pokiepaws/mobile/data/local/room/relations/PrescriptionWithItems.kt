package com.pokiepaws.mobile.data.local.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.pokiepaws.mobile.data.local.room.entities.PrescriptionEntity
import com.pokiepaws.mobile.data.local.room.entities.PrescriptionItemEntity

data class PrescriptionWithItems(
    @Embedded val prescription: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId",
    )
    val items: List<PrescriptionItemEntity>,
)
