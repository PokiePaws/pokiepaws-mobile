package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

        @OptIn(ExperimentalCoroutinesApi::class)
        fun loadVisits(animalId: Long) {
            viewModelScope.launch {
                _uiState.value = AnimalVisitsHistoryUiState.Loading

                visitRepository.getByAnimal(animalId)
                    .onEach(::syncPrescriptions)
                    .flatMapLatest(::visitsWithPrescriptions)
                    .collect { items ->
                        _uiState.value = AnimalVisitsHistoryUiState.Success(items)
                    }
            }
            syncVisits(animalId)
        }

        private fun visitsWithPrescriptions(visits: List<Visit>): Flow<List<AnimalVisitHistoryItem>> =
            if (visits.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    visits.map { visit ->
                        visitRepository.observePrescription(visit.id).map { prescription ->
                            AnimalVisitHistoryItem(visit = visit, prescription = prescription)
                        }
                    },
                ) { items -> items.toList() }
            }

        private fun syncPrescriptions(visits: List<Visit>) {
            viewModelScope.launch {
                visits.forEach { visit ->
                    runCatching { visitRepository.syncPrescription(visit.id) }
                        .onFailure { error ->
                            Log.w(
                                LOG_TAG,
                                "Failed to sync prescription for visit ${visit.id}: ${error.message ?: "unknown error"}",
                                error,
                            )
                        }
                }
            }
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
