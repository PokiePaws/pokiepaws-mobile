package com.pokiepaws.mobile.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.domain.repository.VisitRepository
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOG_TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val visitRepository: VisitRepository,
    ) : ViewModel() {
        val uiState: StateFlow<HomeUiState> =
            visitRepository.getUpcoming()
                .map { visits -> HomeUiState(visitState = VisitListUiState.Success(visits)) }
                .onStart {
                    syncVisits()
                }
                .catch { error ->
                    emit(HomeUiState(visitState = VisitListUiState.Error(error.message ?: "Loading error")))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = HomeUiState(visitState = VisitListUiState.Loading),
                )

        fun syncVisits() {
            viewModelScope.launch {
                runCatching { visitRepository.syncUpcoming() }
                    .onFailure { error ->
                        Log.w(LOG_TAG, "Failed to sync upcoming visits: ${error.message ?: "unknown error"}", error)
                    }
            }
        }
    }
