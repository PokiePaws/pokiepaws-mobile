package com.pokiepaws.mobile.ui.clinics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.domain.model.Vet

@Composable
fun VetListScreen(
    clinicId: Long,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VetsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(clinicId) { viewModel.load(clinicId) }

    Box(modifier = modifier.fillMaxSize()) {
        VetsContent(
            state = state,
            onVetClick = onVetClick,
        )
    }
}

@Composable
private fun VetsContent(
    state: VetsUiState,
    onVetClick: (Long) -> Unit,
) {
    when (state) {
        VetsUiState.Loading -> LoadingView()
        is VetsUiState.Error -> MessageView(text = "Blad: ${state.message}")
        is VetsUiState.Success -> VetsResult(state = state, onVetClick = onVetClick)
    }
}

@Composable
private fun VetsResult(
    state: VetsUiState.Success,
    onVetClick: (Long) -> Unit,
) {
    if (state.vets.isEmpty()) {
        MessageView(text = "Brak weterynarzy w tym gabinecie")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.vets, key = { it.userId }) { vet ->
                VetCard(vet = vet, onClick = { onVetClick(vet.userId) })
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageView(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
private fun VetCard(
    vet: Vet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${vet.firstName} ${vet.lastName}", style = MaterialTheme.typography.titleMedium)
            vet.specialization?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
