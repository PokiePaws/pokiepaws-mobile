package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.VetEntity
import com.pokiepaws.mobile.domain.model.Vet

fun VetEntity.toDomain(): Vet =
    Vet(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )

fun Vet.toEntity(clinicId: Long): VetEntity =
    VetEntity(
        userId = userId,
        clinicId = clinicId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )
