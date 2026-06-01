package com.pokiepaws.mobile.ui.visits.visitdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VisitDetailScreen(
    visitId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VisitDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    LaunchedEffect(visitId) { viewModel.load(visitId) }

    VisitDetailContent(
        state = state,
        isOnline = isOnline,
        onBack = onBack,
        onCancelVisit = { viewModel.cancel(visitId, onDone = onBack) },
        modifier = modifier,
    )
}
