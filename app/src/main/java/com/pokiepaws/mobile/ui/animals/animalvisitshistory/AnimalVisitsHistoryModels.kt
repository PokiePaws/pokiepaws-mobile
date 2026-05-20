package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import com.pokiepaws.mobile.domain.model.Visit

sealed class AnimalVisitsHistoryUiState {
    data object Loading : AnimalVisitsHistoryUiState()

    data class Success(val visits: List<Visit>) : AnimalVisitsHistoryUiState()

    data class Error(val message: String) : AnimalVisitsHistoryUiState()
}
