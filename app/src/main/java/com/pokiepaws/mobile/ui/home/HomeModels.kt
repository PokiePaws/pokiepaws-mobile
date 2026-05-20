package com.pokiepaws.mobile.ui.home

import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState

data class HomeUiState(
    val visitState: VisitListUiState = VisitListUiState.Loading,
)
