package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.VisitEntity
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription

fun VisitEntity.toDomain(): Visit =
    Visit(
        id = id,
        animalId = animalId,
        clinicId = clinicId,
        vetUserId = vetUserId,
        startsAt = startsAt,
        endsAt = endsAt,
        description = description.toVisitDescriptionOrDefault(),
        disease = disease,
        diagnosis = diagnosis,
        recommendations = recommendations,
        status = status,
    )

fun Visit.toEntity(scope: String): VisitEntity =
    VisitEntity(
        scope = scope,
        id = id,
        animalId = animalId,
        clinicId = clinicId,
        vetUserId = vetUserId,
        startsAt = startsAt,
        endsAt = endsAt,
        description = description.name,
        disease = disease,
        diagnosis = diagnosis,
        recommendations = recommendations,
        status = status,
    )

private fun String.toVisitDescriptionOrDefault(): VisitDescription =
    runCatching { VisitDescription.valueOf(this) }
        .getOrDefault(VisitDescription.CHECKUP)
