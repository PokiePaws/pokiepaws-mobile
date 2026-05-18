package com.pokiepaws.mobile.ui.visits.visitlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.ui.animals.animallist.AnimalListViewModel

@Composable
fun VisitListScreen(
    onVisitClick: (Long) -> Unit,
    onCreateVisit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VisitListViewModel = hiltViewModel(),
    animalViewModel: AnimalListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val animalState by animalViewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) { viewModel.loadUpcoming() }

    VisitListContent(
        state = state,
        animalState = animalState,
        onVisitClick = onVisitClick,
        onCreateVisit = onCreateVisit,
        onCancelVisit = viewModel::cancelVisit,
        modifier = modifier,
    )
}
