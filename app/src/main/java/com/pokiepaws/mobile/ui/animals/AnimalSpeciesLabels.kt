package com.pokiepaws.mobile.ui.animals

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.AnimalSpecies
import com.pokiepaws.mobile.domain.model.AnimalSpeciesCategory

@Composable
fun animalSpeciesLabel(species: AnimalSpecies): String =
    when (species) {
        AnimalSpecies.DOG -> stringResource(R.string.animal_species_dog)
        AnimalSpecies.CAT -> stringResource(R.string.animal_species_cat)
        AnimalSpecies.FERRET -> stringResource(R.string.animal_species_ferret)
        AnimalSpecies.RABBIT -> stringResource(R.string.animal_species_rabbit)
        AnimalSpecies.SMALL_MAMMAL -> stringResource(R.string.animal_species_small_mammal)
        AnimalSpecies.HORSE -> stringResource(R.string.animal_species_horse)
        AnimalSpecies.CATTLE -> stringResource(R.string.animal_species_cattle)
        AnimalSpecies.PIG -> stringResource(R.string.animal_species_pig)
        AnimalSpecies.SHEEP -> stringResource(R.string.animal_species_sheep)
        AnimalSpecies.GOAT -> stringResource(R.string.animal_species_goat)
        AnimalSpecies.POULTRY -> stringResource(R.string.animal_species_poultry)
        AnimalSpecies.OTHER -> stringResource(R.string.animal_species_other)
    }

@Composable
fun animalSpeciesCategoryLabel(category: AnimalSpeciesCategory): String =
    when (category) {
        AnimalSpeciesCategory.DOMESTIC -> stringResource(R.string.animal_species_category_domestic)
        AnimalSpeciesCategory.FARM -> stringResource(R.string.animal_species_category_farm)
    }
