package com.pokiepaws.mobile.ui.visits

import com.pokiepaws.mobile.domain.model.Visit

sealed class VisitUiState {
    data object Loading : VisitUiState()

    data class Success(val visits: List<Visit>) : VisitUiState()

    data class Error(val message: String) : VisitUiState()
}
