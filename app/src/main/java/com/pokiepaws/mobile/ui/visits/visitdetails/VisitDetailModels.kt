package com.pokiepaws.mobile.ui.visits.visitdetails

import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit

sealed class VisitDetailUiState {
    data object Loading : VisitDetailUiState()

    data class Success(
        val visit: Visit,
        val clinic: Clinic? = null,
        val prescription: Prescription? = null,
        val isPrescriptionLoading: Boolean = false,
        val prescriptionError: String? = null,
    ) : VisitDetailUiState()

    data class Error(val message: String) : VisitDetailUiState()
}
