package com.pokiepaws.mobile.data.remote.dto.visit

import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import kotlinx.serialization.Serializable

@Serializable
data class VisitResponse(
    val id: Long,
    val animalId: Long,
    val clinicId: Long,
    val vetUserId: Long,
    val startsAt: String,
    val endsAt: String,
    val description: String? = null,
    val disease: String? = null,
    val diagnosis: String? = null,
    val recommendations: String? = null,
    val status: String,
    val used: Boolean,
)

fun VisitResponse.toDomain(): Visit =
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

private fun String?.toVisitDescriptionOrDefault(): VisitDescription =
    this
        ?.let { value -> runCatching { VisitDescription.valueOf(value) }.getOrNull() }
        ?: VisitDescription.CHECKUP
