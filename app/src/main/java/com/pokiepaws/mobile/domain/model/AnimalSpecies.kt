package com.pokiepaws.mobile.domain.model

enum class AnimalSpecies(
    val apiValue: String,
    val category: AnimalSpeciesCategory,
) {
    DOG("DOG", AnimalSpeciesCategory.DOMESTIC),
    CAT("CAT", AnimalSpeciesCategory.DOMESTIC),
    FERRET("FERRET", AnimalSpeciesCategory.DOMESTIC),
    RABBIT("RABBIT", AnimalSpeciesCategory.DOMESTIC),
    SMALL_MAMMAL("SMALL_MAMMAL", AnimalSpeciesCategory.DOMESTIC),
    HORSE("HORSE", AnimalSpeciesCategory.FARM),
    CATTLE("CATTLE", AnimalSpeciesCategory.FARM),
    PIG("PIG", AnimalSpeciesCategory.FARM),
    SHEEP("SHEEP", AnimalSpeciesCategory.FARM),
    GOAT("GOAT", AnimalSpeciesCategory.FARM),
    POULTRY("POULTRY", AnimalSpeciesCategory.FARM),
    OTHER("OTHER", AnimalSpeciesCategory.DOMESTIC),
    ;

    companion object {
        fun fromApiValue(value: String): AnimalSpecies =
            when (value.trim().uppercase()) {
                "DOG", "PIES" -> DOG
                "CAT", "KOT" -> CAT
                "FERRET", "FRETKA" -> FERRET
                "RABBIT", "KRÓLIK", "KROLIK" -> RABBIT
                "SMALL_MAMMAL", "RODENT", "GRYZON", "GRYZONIE", "INNE_MALE_SSAKI" -> SMALL_MAMMAL
                "HORSE", "KOŃ", "KON" -> HORSE
                "CATTLE", "COW", "BYDŁO", "BYDLO" -> CATTLE
                "PIG", "SWINE", "TRZODA_CHLEWNA", "ŚWINIA", "SWINIA" -> PIG
                "SHEEP", "OWCA" -> SHEEP
                "GOAT", "KOZA" -> GOAT
                "POULTRY", "DRÓB", "DROB" -> POULTRY
                else -> OTHER
            }
    }
}
