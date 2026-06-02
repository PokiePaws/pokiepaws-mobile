package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit

data class AnimalVisitHistoryItem(
    val visit: Visit,
    val prescription: Prescription?,
)

sealed class AnimalVisitsHistoryUiState {
    data object Loading : AnimalVisitsHistoryUiState()

    data class Success(val items: List<AnimalVisitHistoryItem>) : AnimalVisitsHistoryUiState()

    data class Error(val message: String) : AnimalVisitsHistoryUiState()
}
