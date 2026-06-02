package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.ui.common.SecureScreen

@Composable
fun AnimalVisitsHistoryScreen(
    animalId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalVisitsHistoryViewModel = hiltViewModel(),
) {
    SecureScreen()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animalId) {
        viewModel.loadVisits(animalId)
    }

    AnimalVisitsHistoryContent(
        state = uiState,
        onBack = onBack,
        modifier = modifier,
    )
}
