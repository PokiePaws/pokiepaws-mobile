package com.pokiepaws.mobile.data.local.room.mappers

import com.pokiepaws.mobile.data.local.room.entities.OwnerProfileEntity
import com.pokiepaws.mobile.domain.model.OwnerProfile

fun OwnerProfileEntity.toDomain(): OwnerProfile =
    OwnerProfile(
        userId = userId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        street = street,
        houseNumber = houseNumber,
        apartmentNumber = apartmentNumber,
        postalCode = postalCode,
        city = city,
        country = country,
    )

fun OwnerProfile.toEntity(): OwnerProfileEntity =
    OwnerProfileEntity(
        userId = userId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        street = street,
        houseNumber = houseNumber,
        apartmentNumber = apartmentNumber,
        postalCode = postalCode,
        city = city,
        country = country,
    )
