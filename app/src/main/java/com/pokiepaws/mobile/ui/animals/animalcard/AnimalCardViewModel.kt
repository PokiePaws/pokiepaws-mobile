package com.pokiepaws.mobile.ui.animals.animalcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
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
                runCatching {
                    val animal = repository.getAnimals().find { it.id == animalId }
                    val travel = appSettingsRepository.foreignTravelPlanned.first()
                    when {
                        animal == null -> AnimalCardUiState.Error("Nie znaleziono zwierzaka")
                        else -> AnimalCardUiState.Success(animal, travel)
                    }
                }.onSuccess { state -> _uiState.value = state }
                    .onFailure { error -> _uiState.value = AnimalCardUiState.Error(error.toLoadMessage()) }
            }
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "Nie udało się załadować szczegółów zwierzaka"
        is IOException -> message ?: "Błąd połączenia z serwerem"
        else -> message ?: "Nie udało się załadować szczegółów zwierzaka"
    }
