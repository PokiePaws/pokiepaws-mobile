package com.pokiepaws.mobile.data.remote.dto.animal

import kotlinx.serialization.Serializable

@Serializable
data class AnimalRequest(
    val name: String,
    val species: String,
    val breed: String? = null,
    val gender: String,
    val color: String? = null,
    val microchipNumber: String? = null,
    val weight: Double? = null,
    val birthDate: String? = null,
    val notes: String? = null,
)
