package com.pokiepaws.mobile.ui.clinics.vetslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "VetsListViewModel"

@HiltViewModel
class VetsListViewModel
    @Inject
    constructor(
        private val vetRepository: VetRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VetsUiState>(VetsUiState.Loading)
        val uiState: StateFlow<VetsUiState> = _uiState

        fun load(clinicId: Long) {
            viewModelScope.launch {
                _uiState.value = VetsUiState.Loading

                vetRepository.getByClinic(clinicId).collect { vets ->
                    _uiState.value = VetsUiState.Success(vets)
                }
            }
            syncVets(clinicId)
        }

        private fun syncVets(clinicId: Long) {
            viewModelScope.launch {
                runCatching { vetRepository.syncByClinic(clinicId) }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync vets: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }
