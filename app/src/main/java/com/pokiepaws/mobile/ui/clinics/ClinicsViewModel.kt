package com.pokiepaws.mobile.ui.clinics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClinicsViewModel
    @Inject
    constructor(
        private val clinicRepository: ClinicRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ClinicsUiState>(ClinicsUiState.Loading)
        val uiState: StateFlow<ClinicsUiState> = _uiState

        fun load() {
            viewModelScope.launch {
                _uiState.value = ClinicsUiState.Loading
                runCatching { clinicRepository.getClinics() }
                    .onSuccess { clinics -> _uiState.value = ClinicsUiState.Success(clinics) }
                    .onFailure { _uiState.value = ClinicsUiState.Error(it.message ?: "Load clinics failed") }
            }
        }
    }
