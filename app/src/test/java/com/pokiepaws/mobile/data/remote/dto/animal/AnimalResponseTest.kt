package com.pokiepaws.mobile.data.remote.dto.animal

import com.pokiepaws.mobile.domain.model.AnimalSpecies
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimalResponseTest {
    @Test
    fun mapsRabiesVaccinationDatesFromBackendContract() {
        val animal =
            AnimalResponse(
                id = 1L,
                name = "Figa",
                species = "DOG",
                gender = "FEMALE",
                rabiesVaccinationDate = "2026-01-10",
                rabiesVaccinationDueDate = "2027-01-10",
            ).toDomain()

        assertEquals(AnimalSpecies.DOG, animal.species)
        assertEquals("2026-01-10", animal.rabiesVaccinationDate)
        assertEquals("2027-01-10", animal.nextRabiesVaccinationDate)
    }

    @Test
    fun keepsLegacyRabiesDueDateFallbacks() {
        val nextDueFromVaccinationAlias =
            AnimalResponse(
                id = 1L,
                name = "Figa",
                species = "DOG",
                gender = "FEMALE",
                rabiesVaccinationNextDueDate = "2027-02-11",
            ).toDomain()

        val nextDueFromShortAlias =
            AnimalResponse(
                id = 2L,
                name = "Luna",
                species = "CAT",
                gender = "FEMALE",
                rabiesNextDueDate = "2027-03-12",
            ).toDomain()

        assertEquals("2027-02-11", nextDueFromVaccinationAlias.nextRabiesVaccinationDate)
        assertEquals("2027-03-12", nextDueFromShortAlias.nextRabiesVaccinationDate)
    }
}
