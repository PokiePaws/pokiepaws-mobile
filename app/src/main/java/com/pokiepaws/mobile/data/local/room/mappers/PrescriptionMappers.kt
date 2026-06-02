package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.PrescriptionEntity
import com.pokiepaws.mobile.data.local.room.entities.PrescriptionItemEntity
import com.pokiepaws.mobile.data.local.room.relations.PrescriptionWithItems
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.PrescriptionItem

fun PrescriptionWithItems.toDomain(): Prescription =
    Prescription(
        id = prescription.id,
        visitId = prescription.visitId,
        vetUserId = prescription.vetUserId,
        clinicId = prescription.clinicId,
        recommendationDate = prescription.recommendationDate,
        creationDate = prescription.creationDate,
        items = items.map { it.toDomain() },
    )

fun Prescription.toEntity(): PrescriptionEntity =
    PrescriptionEntity(
        id = id,
        visitId = visitId,
        vetUserId = vetUserId,
        clinicId = clinicId,
        recommendationDate = recommendationDate,
        creationDate = creationDate,
    )

fun Prescription.toItemEntities(): List<PrescriptionItemEntity> =
    items.map { item ->
        PrescriptionItemEntity(
            prescriptionId = id,
            id = item.id,
            productId = item.productId,
            productName = item.productName,
            quantityPackages = item.quantityPackages,
            dosage = item.dosage,
            treatmentTime = item.treatmentTime,
        )
    }

private fun PrescriptionItemEntity.toDomain(): PrescriptionItem =
    PrescriptionItem(
        id = id,
        productId = productId,
        productName = productName,
        quantityPackages = quantityPackages,
        dosage = dosage,
        treatmentTime = treatmentTime,
    )
