package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "AnimalVisitsHistoryVM"

@HiltViewModel
class AnimalVisitsHistoryViewModel
    @Inject
    constructor(
        private val visitRepository: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AnimalVisitsHistoryUiState>(AnimalVisitsHistoryUiState.Loading)
        val uiState: StateFlow<AnimalVisitsHistoryUiState> = _uiState.asStateFlow()

        fun loadVisits(animalId: Long) {
            viewModelScope.launch {
                _uiState.value = AnimalVisitsHistoryUiState.Loading

                visitRepository.getByAnimal(animalId).collect { visits ->
                    _uiState.value = AnimalVisitsHistoryUiState.Success(visits)
                }
            }
            syncVisits(animalId)
        }

        private fun syncVisits(animalId: Long) {
            viewModelScope.launch {
                runCatching { visitRepository.syncByAnimal(animalId) }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync visit history: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }
