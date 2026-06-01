package com.pokiepaws.mobile.ui.animals.animallist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val LOG_TAG = "AnimalListViewModel"

@HiltViewModel
class AnimalListViewModel
    @Inject
    constructor(
        private val repository: AnimalRepository,
    ) : ViewModel() {
        val uiState: StateFlow<AnimalListUiState> =
            repository.getAnimals()
                .map { animals -> AnimalListUiState.Success(animals) as AnimalListUiState }
                .onStart {
                    emit(AnimalListUiState.Loading)
                    syncAnimals()
                }
                .catch { error -> emit(AnimalListUiState.Error(error.toLoadMessage())) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = AnimalListUiState.Loading,
                )

        fun syncAnimals() {
            viewModelScope.launch {
                runCatching { repository.syncAnimals() }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync animals: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }

private fun Throwable.toLoadMessage(): String =
    when (this) {
        is HttpException -> message ?: "The animal list could not be loaded"
        is IOException -> message ?: "Server connection error"
        else -> message ?: "The list of animals could not be loaded"
    }
