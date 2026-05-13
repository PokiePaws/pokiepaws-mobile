package com.pokiepaws.mobile.ui.visits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitViewModel
    @Inject
    constructor(
        private val repo: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VisitUiState>(VisitUiState.Loading)
        val uiState: StateFlow<VisitUiState> = _uiState

        fun loadUpcoming() {
            viewModelScope.launch {
                _uiState.value = VisitUiState.Loading
                runCatching { repo.getUpcoming() }
                    .onSuccess { _uiState.value = VisitUiState.Success(it) }
                    .onFailure { _uiState.value = VisitUiState.Error(it.message ?: "Unknown error") }
            }
        }

        fun cancelVisit(visitId: Long) {
            viewModelScope.launch {
                runCatching { repo.cancel(visitId) }
                    .onSuccess { loadUpcoming() }
                    .onFailure { _uiState.value = VisitUiState.Error(it.message ?: "Cancel failed") }
            }
        }
    }
