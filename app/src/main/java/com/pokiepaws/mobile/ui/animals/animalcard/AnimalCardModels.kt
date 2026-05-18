package com.pokiepaws.mobile.ui.animals.animalcard

import com.pokiepaws.mobile.domain.model.Animal

sealed class AnimalCardUiState {
    data object Loading : AnimalCardUiState()

    data class Success(
        val animal: Animal,
        val foreignTravelPlanned: Boolean,
    ) : AnimalCardUiState()

    data class Error(val message: String) : AnimalCardUiState()
}
