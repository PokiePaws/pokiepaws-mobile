package com.pokiepaws.mobile.domain.model

data class Prescription(
    val id: Long,
    val visitId: Long,
    val vetUserId: Long,
    val clinicId: Long,
    val recommendationDate: String?,
    val creationDate: String?,
    val items: List<PrescriptionItem>,
)

data class PrescriptionItem(
    val id: Long,
    val productId: Long,
    val productName: String?,
    val quantityPackages: Int?,
    val dosage: String?,
    val treatmentTime: String?,
)
