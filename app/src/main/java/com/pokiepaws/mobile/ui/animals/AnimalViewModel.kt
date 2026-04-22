package com.pokiepaws.mobile.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.remote.AnimalRequest
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnimalUiState {
    object Idle : AnimalUiState()

    object Loading : AnimalUiState()

    data class Success(val animals: List<Animal>) : AnimalUiState()

    data class Error(val message: String) : AnimalUiState()
}

@HiltViewModel
class AnimalViewModel
    @Inject
    constructor(
        private val repository: AnimalRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Idle)
        val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

        init {
            loadAnimals()
        }

        fun loadAnimals() {
            viewModelScope.launch {
                _uiState.value = AnimalUiState.Loading
                try {
                    // Pobieramy listę czystych modeli domenowych
                    val animals = repository.getAnimals()
                    _uiState.value = AnimalUiState.Success(animals)
                } catch (e: Exception) {
                    _uiState.value = AnimalUiState.Error(e.message ?: "Nie udało się załadować listy zwierząt")
                }
            }
        }

        fun addAnimal(
            request: AnimalRequest,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                val result = repository.addAnimal(request)
                result.onSuccess {
                    loadAnimals() // Odświeżamy listę po dodaniu
                    onSuccess()
                }.onFailure { e ->
                    _uiState.value = AnimalUiState.Error(e.message ?: "Błąd podczas dodawania zwierzaka")
                }
            }
        }

        fun deleteAnimal(id: Long) {
            viewModelScope.launch {
                val result = repository.deleteAnimal(id)
                result.onSuccess {
                    loadAnimals() // Odświeżamy listę po usunięciu
                }.onFailure { e ->
                    _uiState.value = AnimalUiState.Error(e.message ?: "Nie udało się usunąć wpisu")
                }
            }
        }
    }
