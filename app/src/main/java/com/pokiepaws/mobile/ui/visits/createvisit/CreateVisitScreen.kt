package com.pokiepaws.mobile.ui.visits.createvisit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateVisitScreen(
    animalId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateVisitViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    CreateVisitContent(
        state = state,
        onBack = onBack,
        modifier = modifier,
        onStepBack = viewModel::goBack,
        onSelectClinic = viewModel::selectClinic,
        onSelectVet = viewModel::selectVet,
        onSelectDate = viewModel::selectDate,
        onSelectSlot = viewModel::selectSlot,
        onDescriptionChange = viewModel::updateDescription,
        onConfirm = { viewModel.confirm(animalId) },
        onClearError = viewModel::clearError,
    )
}
