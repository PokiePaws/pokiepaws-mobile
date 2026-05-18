package com.pokiepaws.mobile.ui.clinics.vetslist

import com.pokiepaws.mobile.domain.model.Vet

sealed class VetsUiState {
    data object Loading : VetsUiState()

    data class Success(val vets: List<Vet>) : VetsUiState()

    data class Error(val message: String) : VetsUiState()
}
