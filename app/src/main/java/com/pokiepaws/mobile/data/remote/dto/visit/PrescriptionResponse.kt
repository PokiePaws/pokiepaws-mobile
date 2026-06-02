package com.pokiepaws.mobile.data.remote.dto.visit

import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.PrescriptionItem
import kotlinx.serialization.Serializable

@Serializable
data class PrescriptionResponse(
    val id: Long,
    val visitId: Long,
    val vetUserId: Long,
    val clinicId: Long,
    val recommendationDate: String? = null,
    val creationDate: String? = null,
    val items: List<PrescriptionItemResponse> = emptyList(),
)

@Serializable
data class PrescriptionItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String? = null,
    val quantityPackages: Int? = null,
    val dosage: String? = null,
    val treatmentTime: String? = null,
)

fun PrescriptionResponse.toDomain(): Prescription =
    Prescription(
        id = id,
        visitId = visitId,
        vetUserId = vetUserId,
        clinicId = clinicId,
        recommendationDate = recommendationDate,
        creationDate = creationDate,
        items = items.map { it.toDomain() },
    )

private fun PrescriptionItemResponse.toDomain(): PrescriptionItem =
    PrescriptionItem(
        id = id,
        productId = productId,
        productName = productName,
        quantityPackages = quantityPackages,
        dosage = dosage,
        treatmentTime = treatmentTime,
    )
