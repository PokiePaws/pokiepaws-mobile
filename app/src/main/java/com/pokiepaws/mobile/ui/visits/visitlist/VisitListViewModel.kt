package com.pokiepaws.mobile.ui.visits.visitlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "VisitListViewModel"

@HiltViewModel
class VisitListViewModel
    @Inject
    constructor(
        private val repo: VisitRepository,
        connectivityObserver: ConnectivityObserver,
    ) : ViewModel() {
        val isOnline: StateFlow<Boolean> =
            connectivityObserver.isOnline.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = connectivityObserver.isOnline.value,
            )

        val uiState: StateFlow<VisitListUiState> =
            repo.getUpcoming()
                .map { visits -> VisitListUiState.Success(visits) as VisitListUiState }
                .onStart {
                    syncUpcoming()
                }
                .catch { error -> emit(VisitListUiState.Error(error.message ?: "Unknown error")) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = VisitListUiState.Loading,
                )

        fun syncUpcoming() {
            viewModelScope.launch {
                runCatching { repo.syncUpcoming() }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync upcoming visits: ${error.message ?: "unknown error"}", error)
                    }
            }
        }

        fun cancelVisit(visitId: Long) {
            if (!isOnline.value) {
                Log.w(LOG_TAG, "Cannot cancel visit without internet connection")
                return
            }

            viewModelScope.launch {
                runCatching { repo.cancel(visitId) }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to cancel visit: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }
