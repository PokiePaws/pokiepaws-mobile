package com.pokiepaws.mobile.ui.visits.visitlist

import com.pokiepaws.mobile.domain.model.Visit

sealed class VisitListUiState {
    data object Loading : VisitListUiState()

    data class Success(val visits: List<Visit>) : VisitListUiState()

    data class Error(val message: String) : VisitListUiState()
}
