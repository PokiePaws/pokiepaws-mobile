package com.pokiepaws.mobile.domain.model

data class CreateVisitDraft(
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val description: String? = null,
)
