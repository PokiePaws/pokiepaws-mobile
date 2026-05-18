package com.pokiepaws.mobile.ui.animals.animallist

import androidx.compose.runtime.Immutable
import com.pokiepaws.mobile.domain.model.Animal

@Immutable
data class AnimalItems(val items: List<Animal>)

sealed class AnimalListUiState {
    data object Loading : AnimalListUiState()

    data class Success(val animals: List<Animal>) : AnimalListUiState()

    data class Error(val message: String) : AnimalListUiState()
}
