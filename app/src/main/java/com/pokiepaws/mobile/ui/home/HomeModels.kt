package com.pokiepaws.mobile.ui.home

import com.pokiepaws.mobile.ui.animals.animallist.AnimalListUiState
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState

data class HomeUiState(
    val animalState: AnimalListUiState = AnimalListUiState.Loading,
    val visitState: VisitListUiState = VisitListUiState.Loading,
)
