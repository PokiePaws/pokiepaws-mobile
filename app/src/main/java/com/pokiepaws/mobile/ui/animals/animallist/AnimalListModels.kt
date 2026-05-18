package com.pokiepaws.mobile.ui.animals.animallist

import com.pokiepaws.mobile.domain.model.Animal

sealed class AnimalListUiState {
    data object Loading : AnimalListUiState()

    data class Success(val animals: List<Animal>) : AnimalListUiState()

    data class Error(val message: String) : AnimalListUiState()
}
