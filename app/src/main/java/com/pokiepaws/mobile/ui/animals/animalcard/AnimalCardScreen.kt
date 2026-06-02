package com.pokiepaws.mobile.ui.animals.animalcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.ui.common.SecureScreen

@Composable
fun AnimalCardScreen(
    animalId: Long,
    onBack: () -> Unit,
    onVisitsHistoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalCardViewModel = hiltViewModel(),
) {
    SecureScreen()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is AnimalCardUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is AnimalCardUiState.Success -> {
                AnimalDetailsContent(
                    animal = state.animal,
                    foreignTravelPlanned = state.foreignTravelPlanned,
                    onVisitsHistoryClick = { onVisitsHistoryClick(state.animal.id) },
                    onBack = onBack,
                )
            }

            is AnimalCardUiState.Error -> {
                androidx.compose.material3.Text(
                    text = state.message,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
