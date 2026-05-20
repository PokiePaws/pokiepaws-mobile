package com.pokiepaws.mobile.ui.visits.createvisit

import androidx.compose.runtime.Immutable
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.CreateVisitStep
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.model.VisitDescription

@Immutable
data class ClinicItems(val items: List<Clinic>)

@Immutable
data class VetItems(val items: List<Vet>)

@Immutable
data class SlotItems(val items: List<String>)

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
    val description: VisitDescription = VisitDescription.CHECKUP,
    val success: Boolean = false,
    val error: String? = null,
)
