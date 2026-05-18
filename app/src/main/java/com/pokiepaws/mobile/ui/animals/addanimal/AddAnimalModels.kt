package com.pokiepaws.mobile.ui.animals.addanimal

sealed class AddAnimalUiState {
    object Idle : AddAnimalUiState()

    object Loading : AddAnimalUiState()

    object Success : AddAnimalUiState()

    data class Error(val message: String) : AddAnimalUiState()
}
