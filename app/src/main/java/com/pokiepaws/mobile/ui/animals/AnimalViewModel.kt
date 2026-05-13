package com.pokiepaws.mobile.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
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
                runCatching { repository.getAnimals() }
                    .onSuccess { animals -> _uiState.value = AnimalUiState.Success(animals) }
                    .onFailure { error -> _uiState.value = AnimalUiState.Error(error.toLoadMessage()) }
            }
        }

        fun addAnimal(
            animal: AnimalDraft,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                runCatching { repository.addAnimal(animal) }
                    .onSuccess {
                        loadAnimals()
                        onSuccess()
                    }
                    .onFailure { error ->
                        _uiState.value = AnimalUiState.Error(error.message ?: "Blad podczas dodawania zwierzaka")
                    }
            }
        }

        fun deleteAnimal(id: Long) {
            viewModelScope.launch {
                runCatching { repository.deleteAnimal(id) }
                    .onSuccess { loadAnimals() }
                    .onFailure { error ->
                        _uiState.value = AnimalUiState.Error(error.message ?: "Nie udalo sie usunac wpisu")
                    }
            }
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "Nie udalo sie zaladowac listy zwierzat"
        is IOException -> message ?: "Blad polaczenia z serwerem"
        else -> message ?: "Nie udalo sie zaladowac listy zwierzat"
    }
