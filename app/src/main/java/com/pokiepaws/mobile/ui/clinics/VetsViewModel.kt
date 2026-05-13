package com.pokiepaws.mobile.ui.clinics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetsViewModel
    @Inject
    constructor(
        private val vetRepository: VetRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VetsUiState>(VetsUiState.Loading)
        val uiState: StateFlow<VetsUiState> = _uiState

        fun load(clinicId: Long) {
            viewModelScope.launch {
                _uiState.value = VetsUiState.Loading
                runCatching { vetRepository.getByClinic(clinicId) }
                    .onSuccess { vets -> _uiState.value = VetsUiState.Success(vets) }
                    .onFailure { _uiState.value = VetsUiState.Error(it.message ?: "Load vets failed") }
            }
        }
    }
