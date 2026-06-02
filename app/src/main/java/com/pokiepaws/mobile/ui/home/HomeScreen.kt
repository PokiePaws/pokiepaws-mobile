package com.pokiepaws.mobile.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAppointments: () -> Unit = {},
    onVisitClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        visitState = state.visitState,
        onNavigateToNotifications = onNavigateToNotifications,
        modifier = modifier,
        onNavigateToAppointments = onNavigateToAppointments,
        onVisitClick = onVisitClick,
        animalNamesById = state.animalNamesById,
    )
}
