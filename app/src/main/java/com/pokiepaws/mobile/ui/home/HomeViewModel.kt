package com.pokiepaws.mobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import com.pokiepaws.mobile.domain.repository.VisitRepository
import com.pokiepaws.mobile.ui.animals.animallist.AnimalListUiState
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val animalRepository: AnimalRepository,
        private val visitRepository: VisitRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            loadHomeData()
        }

        fun loadHomeData() {
            loadAnimals()
            loadVisits()
        }

        private fun loadAnimals() {
            viewModelScope.launch {
                _uiState.update { it.copy(animalState = AnimalListUiState.Loading) }
                runCatching { animalRepository.getAnimals() }
                    .onSuccess { animals ->
                        _uiState.update { it.copy(animalState = AnimalListUiState.Success(animals)) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(animalState = AnimalListUiState.Error(error.message ?: "Loading error"))
                        }
                    }
            }
        }

        private fun loadVisits() {
            viewModelScope.launch {
                _uiState.update { it.copy(visitState = VisitListUiState.Loading) }
                runCatching { visitRepository.getUpcoming() }
                    .onSuccess { visits ->
                        _uiState.update { it.copy(visitState = VisitListUiState.Success(visits)) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(visitState = VisitListUiState.Error(error.message ?: "Loading error"))
                        }
                    }
            }
        }
    }
