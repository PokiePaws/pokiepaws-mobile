package com.pokiepaws.mobile.ui.animals.animallist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AnimalListViewModel
    @Inject
    constructor(
        private val repository: AnimalRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AnimalListUiState>(AnimalListUiState.Loading)
        val uiState: StateFlow<AnimalListUiState> = _uiState

        init {
            loadAnimals()
        }

        fun loadAnimals() {
            viewModelScope.launch {
                _uiState.value = AnimalListUiState.Loading
                runCatching { repository.getAnimals() }
                    .onSuccess { animals -> _uiState.value = AnimalListUiState.Success(animals) }
                    .onFailure { error -> _uiState.value = AnimalListUiState.Error(error.toLoadMessage()) }
            }
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "Nie udało się załadować listy zwierząt"
        is IOException -> message ?: "Błąd połączenia z serwerem"
        else -> message ?: "Nie udało się załadować listy zwierząt"
    }
