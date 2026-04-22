package com.pokiepaws.mobile.ui.animals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnimalScreen(
    animalId: Long,
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Błędy 71:33 i 76:44 poprawione poprzez przeniesienie komentarzy nad linię kodu
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        when (uiState) {
            is AnimalUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is AnimalUiState.Success -> {
                // Tutaj logika wyświetlania szczegółów zwierzaka
                Text(
                    text = "Szczegóły zwierzaka o ID: $animalId",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            is AnimalUiState.Error -> {
                // Wyświetlanie błędu
                Text(
                    text = (uiState as AnimalUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            else -> {}
        }
    }
}
