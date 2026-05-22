package com.pokiepaws.mobile.ui.animals.addanimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAnimalViewModel
    @Inject
    constructor(
        private val animalRepository: AnimalRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AddAnimalUiState>(AddAnimalUiState.Idle)
        val uiState: StateFlow<AddAnimalUiState> = _uiState

        fun addAnimal(
            animal: AnimalDraft,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                _uiState.value = AddAnimalUiState.Loading
                runCatching { animalRepository.addAnimal(animal) }
                    .onSuccess {
                        _uiState.value = AddAnimalUiState.Success
                        onSuccess()
                    }
                    .onFailure { error ->
                        _uiState.value = AddAnimalUiState.Error(error.message ?: "Error while adding an animal")
                    }
            }
        }
    }
