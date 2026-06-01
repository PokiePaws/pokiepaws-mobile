package com.pokiepaws.mobile.ui.visits.visitdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "VisitDetailViewModel"

@HiltViewModel
class VisitDetailViewModel
    @Inject
    constructor(
        private val repo: VisitRepository,
        connectivityObserver: ConnectivityObserver,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VisitDetailUiState>(VisitDetailUiState.Loading)
        val uiState: StateFlow<VisitDetailUiState> = _uiState.asStateFlow()
        val isOnline: StateFlow<Boolean> =
            connectivityObserver.isOnline.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = connectivityObserver.isOnline.value,
            )

        fun load(visitId: Long) {
            viewModelScope.launch {
                _uiState.value = VisitDetailUiState.Loading

                repo.getById(visitId).collect { visit ->
                    if (visit != null) {
                        _uiState.value = VisitDetailUiState.Success(visit)
                    } else {
                        _uiState.value = VisitDetailUiState.Error("Visit not found")
                    }
                }
            }
            syncVisit(visitId)
        }

        private fun syncVisit(visitId: Long) {
            viewModelScope.launch {
                runCatching { repo.syncById(visitId) }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync visit details: ${error.message ?: "unknown error"}", error)
                    }
            }
        }

        fun cancel(
            visitId: Long,
            onDone: () -> Unit,
        ) {
            if (!isOnline.value) {
                _uiState.value = VisitDetailUiState.Error(ONLINE_ACTION_REQUIRED_MESSAGE)
                return
            }

            viewModelScope.launch {
                runCatching { repo.cancel(visitId) }
                    .onSuccess { onDone() }
                    .onFailure { _uiState.value = VisitDetailUiState.Error(it.message ?: "Cancel error") }
            }
        }
    }

private const val ONLINE_ACTION_REQUIRED_MESSAGE = "Ta akcja wymaga połączenia z internetem"
