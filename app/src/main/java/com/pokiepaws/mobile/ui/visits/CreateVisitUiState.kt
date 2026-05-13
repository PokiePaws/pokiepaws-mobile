package com.pokiepaws.mobile.ui.visits

import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Vet

data class CreateVisitUiState(
    val step: CreateVisitStep = CreateVisitStep.SELECT_CLINIC,
    val isLoading: Boolean = false,
    val clinics: List<Clinic> = emptyList(),
    val vets: List<Vet> = emptyList(),
    val selectedClinic: Clinic? = null,
    val selectedVet: Vet? = null,
    val selectedDate: String? = null,
    val availableSlots: List<String> = emptyList(),
    val selectedSlot: String? = null,
    val description: String = "",
    val success: Boolean = false,
    val error: String? = null,
)
