package com.pokiepaws.mobile.ui.visits.visitdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitDetailViewModel
    @Inject
    constructor(
        private val repo: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VisitDetailUiState>(VisitDetailUiState.Loading)
        val uiState: StateFlow<VisitDetailUiState> = _uiState.asStateFlow()

        fun load(visitId: Long) {
            viewModelScope.launch {
                _uiState.value = VisitDetailUiState.Loading
                runCatching { repo.getById(visitId) }
                    .onSuccess { _uiState.value = VisitDetailUiState.Success(it) }
                    .onFailure { _uiState.value = VisitDetailUiState.Error(it.message ?: "Błąd") }
            }
        }

        fun cancel(
            visitId: Long,
            onDone: () -> Unit,
        ) {
            viewModelScope.launch {
                runCatching { repo.cancel(visitId) }
                    .onSuccess { onDone() }
                    .onFailure { _uiState.value = VisitDetailUiState.Error(it.message ?: "Błąd anulowania") }
            }
        }
    }
