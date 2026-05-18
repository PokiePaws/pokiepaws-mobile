package com.pokiepaws.mobile.ui.visits.visitdetails

import com.pokiepaws.mobile.domain.model.Visit

sealed class VisitDetailUiState {
    data object Loading : VisitDetailUiState()

    data class Success(val visit: Visit) : VisitDetailUiState()

    data class Error(val message: String) : VisitDetailUiState()
}
