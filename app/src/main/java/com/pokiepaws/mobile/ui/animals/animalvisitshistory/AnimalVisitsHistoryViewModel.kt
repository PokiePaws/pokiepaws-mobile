package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

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
                runCatching { visitRepository.getByAnimal(animalId) }
                    .onSuccess { visits ->
                        _uiState.value = AnimalVisitsHistoryUiState.Success(visits)
                    }
                    .onFailure { error ->
                        _uiState.value = AnimalVisitsHistoryUiState.Error(error.toLoadMessage())
                    }
            }
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "Failed to load visit history"
        is IOException -> message ?: "Server connection error"
        else -> message ?: "Failed to load visit history"
    }
