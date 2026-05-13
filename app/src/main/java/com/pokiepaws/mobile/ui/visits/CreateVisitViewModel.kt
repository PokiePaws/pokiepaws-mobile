package com.pokiepaws.mobile.ui.visits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import com.pokiepaws.mobile.data.remote.dto.vet.VetListResponse
import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

private const val FIRST_VISIT_HOUR = 9
private const val LAST_VISIT_HOUR = 16
private const val VISIT_INTERVAL_MINUTES = 30L

@HiltViewModel
class CreateVisitViewModel
    @Inject
    constructor(
        private val clinicApi: ClinicApiService,
        private val vetApi: VetApiService,
        private val visitRepository: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CreateVisitUiState())
        val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()

        fun loadClinics() {
            if (_uiState.value.clinics.isNotEmpty()) return

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                runCatching { clinicApi.getAll().map { response -> response.toDomain() } }
                    .onSuccess { clinics ->
                        _uiState.update { it.copy(isLoading = false, clinics = clinics) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udało się załadować gabinetów")
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
            val parsedDate =
                runCatching { LocalDate.parse(date) }
                    .getOrElse {
                        _uiState.update { state -> state.copy(error = "Podaj datę w formacie YYYY-MM-DD") }
                        return
                    }

            _uiState.update {
                it.copy(
                    selectedDate = date,
                    availableSlots = generateSlots(parsedDate),
                    selectedSlot = null,
                    error = null,
                )
            }
        }

        fun selectSlot(slot: String) {
            _uiState.update {
                it.copy(step = CreateVisitStep.CONFIRM, selectedSlot = slot, error = null)
            }
        }

        fun updateDescription(description: String) {
            _uiState.update { it.copy(description = description) }
        }

        fun confirm(animalId: Long) {
            val state = _uiState.value
            val clinic = state.selectedClinic
            val vet = state.selectedVet
            val slot = state.selectedSlot

            if (clinic == null || vet == null || slot == null) {
                _uiState.update { it.copy(error = "Uzupełnij wszystkie dane wizyty") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val request =
                    CreateVisitRequest(
                        animalId = animalId,
                        clinicId = clinic.id,
                        vetUserId = vet.userId,
                        startsAt = slot,
                        description = state.description.takeIf { it.isNotBlank() },
                    )

                runCatching { visitRepository.create(request) }
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, success = true) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udało się umówić wizyty")
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
                runCatching { vetApi.getByClinicList(clinicId).map { response -> response.toDomain() } }
                    .onSuccess { vets ->
                        _uiState.update { it.copy(isLoading = false, vets = vets) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Nie udało się załadować weterynarzy")
                        }
                    }
            }
        }

        private fun generateSlots(date: LocalDate): List<String> {
            val start = LocalTime.of(FIRST_VISIT_HOUR, 0)
            val end = LocalTime.of(LAST_VISIT_HOUR, 30)
            return generateSequence(start) { it.plusMinutes(VISIT_INTERVAL_MINUTES) }
                .takeWhile { !it.isAfter(end) }
                .map { time -> "${date}T${time}:00" }
                .toList()
        }
    }

private fun ClinicResponse.toDomain(): Clinic =
    Clinic(
        id = id,
        clinicName = clinicName,
        city = city,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        country = country,
        phone = phone,
        email = email,
    )

private fun VetListResponse.toDomain(): Vet =
    Vet(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        specialization = specialization,
    )
