package com.pokiepaws.mobile.domain.model

data class Vet(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val specialization: String? = null,
)