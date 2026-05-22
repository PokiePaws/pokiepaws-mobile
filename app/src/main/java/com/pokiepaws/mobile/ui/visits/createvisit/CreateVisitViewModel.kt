package com.pokiepaws.mobile.ui.visits.createvisit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.CreateVisitStep
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import com.pokiepaws.mobile.domain.repository.VetRepository
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateVisitViewModel
    @Inject
    constructor(
        private val clinicRepository: ClinicRepository,
        private val vetRepository: VetRepository,
        private val visitRepository: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CreateVisitUiState())
        val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()

        fun loadClinics() {
            if (_uiState.value.clinics.isEmpty()) {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    runCatching { clinicRepository.getClinics() }
                        .onSuccess { clinics ->
                            _uiState.update { it.copy(isLoading = false, clinics = clinics) }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(isLoading = false, error = error.message ?: "Nie udalo sie zaladowac gabinetow")
                            }
                        }
                }
            }
        }

        fun selectClinic(clinic: Clinic) {
            _uiState.update {
                it.copy(
                    step = CreateVisitStep.SELECT_VET,
                    selectedClinic = clinic,
                    selectedVet = null,
                    vets = emptyList(),
                    selectedDate = null,
                    availableSlots = emptyList(),
                    selectedSlot = null,
                    error = null,
                )
            }
            loadVets(clinic.id)
        }

        fun selectVet(vet: Vet) {
            _uiState.update {
                it.copy(
                    step = CreateVisitStep.SELECT_SLOT,
                    selectedVet = vet,
                    selectedDate = null,
                    availableSlots = emptyList(),
                    selectedSlot = null,
                    error = null,
                )
            }
        }

        fun selectDate(date: String) {
            val parsedDate = parseVisitDate(date)
            val clinic = _uiState.value.selectedClinic
            val vet = _uiState.value.selectedVet

            if (parsedDate == null) {
                _uiState.update { state -> state.copy(error = "Podaj date w formacie YYYY-MM-DD") }
            } else if (parsedDate.isBefore(LocalDate.now())) {
                _uiState.update { state -> state.copy(error = "Nie mozna wybrac daty z przeszlosci") }
            } else if (clinic == null || vet == null) {
                _uiState.update { state -> state.copy(error = "Wybierz gabinet i weterynarza") }
            } else {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            selectedDate = date,
                            availableSlots = emptyList(),
                            selectedSlot = null,
                            isLoading = true,
                            error = null,
                        )
                    }
                    runCatching {
                        visitRepository.getAvailableSlots(
                            clinicId = clinic.id,
                            vetUserId = vet.userId,
                            date = date,
                        )
                    }.onSuccess { slots ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                availableSlots = slots,
                                error = null,
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                availableSlots = emptyList(),
                                error = error.message ?: "Nie udalo sie zaladowac dostepnych terminow",
                            )
                        }
                    }
                }
            }
        }

        fun selectSlot(slot: String) {
            _uiState.update {
                it.copy(step = CreateVisitStep.CONFIRM, selectedSlot = slot, error = null)
            }
        }

        fun updateDescription(description: VisitDescription) {
            _uiState.update { it.copy(description = description) }
        }

        fun confirm(animalId: Long) {
            val request = _uiState.value.toCreateVisitDraft(animalId)

            if (request == null) {
                _uiState.update { it.copy(error = "Uzupelnij wszystkie dane wizyty") }
            } else {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    runCatching { visitRepository.create(request) }
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false, success = true) }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(isLoading = false, error = error.message ?: "Nie udalo sie umowic wizyty")
                            }
                        }
                }
            }
        }

        fun goBack() {
            _uiState.update { state ->
                when (state.step) {
                    CreateVisitStep.SELECT_VET ->
                        state.copy(step = CreateVisitStep.SELECT_CLINIC, selectedClinic = null, vets = emptyList())

                    CreateVisitStep.SELECT_SLOT ->
                        state.copy(
                            step = CreateVisitStep.SELECT_VET,
                            selectedVet = null,
                            selectedDate = null,
                            availableSlots = emptyList(),
                            selectedSlot = null,
                        )

                    CreateVisitStep.CONFIRM ->
                        state.copy(step = CreateVisitStep.SELECT_SLOT, selectedSlot = null)

                    CreateVisitStep.SELECT_CLINIC,
                    CreateVisitStep.SELECT_ANIMAL,
                    -> state
                }
            }
        }

        fun clearError() {
            _uiState.update { it.copy(error = null) }
        }

        private fun loadVets(clinicId: Long) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                runCatching { vetRepository.getByClinic(clinicId) }
                    .onSuccess { vets ->
                        _uiState.update { it.copy(isLoading = false, vets = vets) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udalo sie zaladowac weterynarzy")
                        }
                    }
            }
        }
    }

private fun parseVisitDate(date: String): LocalDate? = runCatching { LocalDate.parse(date) }.getOrNull()

private fun CreateVisitUiState.toCreateVisitDraft(animalId: Long): CreateVisitDraft? {
    val clinic = selectedClinic
    val vet = selectedVet
    val slot = selectedSlot

    return if (clinic == null || vet == null || slot == null) {
        null
    } else {
        CreateVisitDraft(
            animalId = animalId,
            clinicId = clinic.id,
            vetUserId = vet.userId,
            startsAt = slot,
            description = description,
        )
    }
}
