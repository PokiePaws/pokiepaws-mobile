package com.pokiepaws.mobile.ui.visits.createvisit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
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
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateVisitViewModel
    @Inject
    constructor(
        private val clinicRepository: ClinicRepository,
        private val vetRepository: VetRepository,
        private val visitRepository: VisitRepository,
        private val connectivityObserver: ConnectivityObserver,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CreateVisitUiState(isOnline = connectivityObserver.isOnline.value))
        val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()

        init {
            observeInitialData()
            syncClinics()
        }

        private fun observeInitialData() {
            viewModelScope.launch {
                connectivityObserver.isOnline.collect { online ->
                    _uiState.update { it.copy(isOnline = online) }
                }
            }
            viewModelScope.launch {
                clinicRepository.getClinics().collect { clinics ->
                    _uiState.update { it.copy(clinics = clinics) }
                }
            }
        }

        private fun syncClinics() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                runCatching { clinicRepository.syncClinics() }
                    .onSuccess { _uiState.update { it.copy(isLoading = false) } }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udalo sie zaladowac gabinetow")
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
            observeVets(clinic.id)
            syncVets(clinic.id)
        }

        private fun observeVets(clinicId: Long) {
            viewModelScope.launch {
                vetRepository.getByClinic(clinicId).collect { vets ->
                    _uiState.update { it.copy(vets = vets) }
                }
            }
        }

        private fun syncVets(clinicId: Long) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                runCatching { vetRepository.syncByClinic(clinicId) }
                    .onSuccess { _uiState.update { it.copy(isLoading = false) } }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udalo sie zaladowac weterynarzy")
                        }
                    }
            }
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
            if (!connectivityObserver.isOnline.value) {
                _uiState.update { it.copy(error = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }

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
                        val availableFutureSlots = slots.futureSlots()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                availableSlots = availableFutureSlots,
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
            if (!slot.isFutureSlot()) {
                _uiState.update { it.copy(error = "Wybrana godzina juz minela. Wybierz najblizszy dostepny termin.") }
                return
            }

            _uiState.update {
                it.copy(step = CreateVisitStep.CONFIRM, selectedSlot = slot, error = null)
            }
        }

        fun updateDescription(description: VisitDescription) {
            _uiState.update { it.copy(description = description) }
        }

        fun confirm(animalId: Long) {
            if (!connectivityObserver.isOnline.value) {
                _uiState.update { it.copy(error = ONLINE_ACTION_REQUIRED_MESSAGE) }
                return
            }

            val request = _uiState.value.toCreateVisitDraft(animalId)
            val selectedSlot = _uiState.value.selectedSlot

            if (selectedSlot != null && !selectedSlot.isFutureSlot()) {
                _uiState.update {
                    it.copy(
                        step = CreateVisitStep.SELECT_SLOT,
                        selectedSlot = null,
                        error = "Wybrana godzina juz minela. Wybierz najblizszy dostepny termin.",
                    )
                }
            } else if (request == null) {
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
    }

private const val ONLINE_ACTION_REQUIRED_MESSAGE = "Ta akcja wymaga połączenia z internetem"

private fun parseVisitDate(date: String): LocalDate? = runCatching { LocalDate.parse(date) }.getOrNull()

private fun List<String>.futureSlots(): List<String> =
    filter { it.isFutureSlot() }
        .sortedBy { it.toSlotDateTimeOrNull() }

private fun String.isFutureSlot(now: LocalDateTime = LocalDateTime.now()): Boolean {
    return toSlotDateTimeOrNull()?.isAfter(now) == true
}

private fun String.toSlotDateTimeOrNull(): LocalDateTime? =
    runCatching { LocalDateTime.parse(this) }
        .recoverCatching { LocalDateTime.parse(substringBefore("+")) }
        .getOrNull()

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
