package com.pokiepaws.mobile.domain.model

data class Visit(
    val id: Long,
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val endsAt: String,
    val description: VisitDescription,
    val disease: String?,
    val diagnosis: String?,
    val recommendations: String?,
    val status: String,
)
