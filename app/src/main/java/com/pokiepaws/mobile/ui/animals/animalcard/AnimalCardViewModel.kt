package com.pokiepaws.mobile.ui.animals.animalcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalCardViewModel
    @Inject
    constructor(
        private val repository: AnimalRepository,
        private val appSettingsRepository: AppSettingsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AnimalCardUiState>(AnimalCardUiState.Loading)
        val uiState: StateFlow<AnimalCardUiState> = _uiState

        fun loadAnimal(animalId: Long) {
            viewModelScope.launch {
                _uiState.value = AnimalCardUiState.Loading

                repository.getAnimal(animalId)
                    .combine(appSettingsRepository.foreignTravelPlanned) { animal, travel ->
                        when {
                            animal == null -> AnimalCardUiState.Error("Animal not found")
                            else -> AnimalCardUiState.Success(animal, travel)
                        }
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            }
        }
    }
