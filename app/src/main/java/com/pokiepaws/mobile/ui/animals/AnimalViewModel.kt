package com.pokiepaws.mobile.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
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

    data class Success(
        val animals: List<Animal>,
        val foreignTravelPlanned: Boolean,
    ) : AnimalUiState()

    data class Error(val message: String) : AnimalUiState()
}

@HiltViewModel
class AnimalViewModel
    @Inject
    constructor(
        private val repository: AnimalRepository,
        private val appSettingsRepository: AppSettingsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Idle)
        val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

        private var animalsCache: List<Animal> = emptyList()
        private var foreignTravelPlanned: Boolean = false

        init {
            observeForeignTravelSetting()
            loadAnimals()
        }

        fun loadAnimals() {
            viewModelScope.launch {
                _uiState.value = AnimalUiState.Loading
                runCatching { repository.getAnimals() }
                    .onSuccess { animals ->
                        animalsCache = animals
                        publishSuccess()
                    }
                    .onFailure { error -> _uiState.value = AnimalUiState.Error(error.toLoadMessage()) }
            }
        }

        fun setForeignTravelPlanned(value: Boolean) {
            viewModelScope.launch {
                appSettingsRepository.setForeignTravelPlanned(value)
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

        private fun observeForeignTravelSetting() {
            viewModelScope.launch {
                appSettingsRepository.foreignTravelPlanned.collect { value ->
                    foreignTravelPlanned = value
                    if (animalsCache.isNotEmpty()) {
                        publishSuccess()
                    }
                }
            }
        }

        private fun publishSuccess() {
            _uiState.value =
                AnimalUiState.Success(
                    animals = animalsCache,
                    foreignTravelPlanned = foreignTravelPlanned,
                )
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "Nie udalo sie zaladowac listy zwierzat"
        is IOException -> message ?: "Blad polaczenia z serwerem"
        else -> message ?: "Nie udalo sie zaladowac listy zwierzat"
    }
