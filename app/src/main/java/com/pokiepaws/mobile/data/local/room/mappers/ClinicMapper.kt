package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.ClinicEntity
import com.pokiepaws.mobile.domain.model.Clinic

fun ClinicEntity.toDomain(): Clinic =
    Clinic(
        id = id,
        clinicName = clinicName,
        city = city,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        country = country,
        phone = phone,
        email = email,
    )

fun Clinic.toEntity(): ClinicEntity =
    ClinicEntity(
        id = id,
        clinicName = clinicName,
        city = city,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        country = country,
        phone = phone,
        email = email,
    )
