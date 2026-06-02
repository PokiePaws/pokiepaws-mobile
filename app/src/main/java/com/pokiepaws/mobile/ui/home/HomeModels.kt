package com.pokiepaws.mobile.ui.home

import androidx.compose.runtime.Immutable
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState

@Immutable
data class HomeAnimalNames(val items: Map<Long, String> = emptyMap())

data class HomeUiState(
    val visitState: VisitListUiState = VisitListUiState.Loading,
    val animalNamesById: HomeAnimalNames = HomeAnimalNames(),
)
