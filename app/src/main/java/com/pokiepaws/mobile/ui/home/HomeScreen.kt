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
    onNavigateToAnimals: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToClinics: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        animalState = state.animalState,
        visitState = state.visitState,
        onNavigateToNotifications = onNavigateToNotifications,
        modifier = modifier,
        onNavigateToAnimals = onNavigateToAnimals,
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToClinics = onNavigateToClinics,
    )
}
