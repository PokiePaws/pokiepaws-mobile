package com.pokiepaws.mobile.ui.clinics.clinicslist

import com.pokiepaws.mobile.domain.model.Clinic

sealed class ClinicsUiState {
    data object Loading : ClinicsUiState()

    data class Success(val clinics: List<Clinic>) : ClinicsUiState()

    data class Error(val message: String) : ClinicsUiState()
}
