package com.pokiepaws.mobile.ui.clinics.clinicslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "ClinicsListViewModel"

@HiltViewModel
class ClinicsListViewModel
    @Inject
    constructor(
        private val clinicRepository: ClinicRepository,
    ) : ViewModel() {
        val uiState: StateFlow<ClinicsUiState> =
            clinicRepository.getClinics()
                .map { clinics -> ClinicsUiState.Success(clinics) as ClinicsUiState }
                .onStart {
                    sync()
                }
                .catch { error -> emit(ClinicsUiState.Error(error.message ?: "Load clinics failed")) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = ClinicsUiState.Loading,
                )

        fun sync() {
            viewModelScope.launch {
                runCatching { clinicRepository.syncClinics() }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync clinics: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }
