package com.pokiepaws.mobile.domain.model

data class Animal(
    val id: Long,
    val name: String,
    val species: String,
    val breed: String?,
    val weight: Double?,
    val birthDate: String?,
    val notes: String?,
)
