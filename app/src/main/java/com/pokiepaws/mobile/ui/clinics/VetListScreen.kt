package com.pokiepaws.mobile.ui.clinics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

    when (val s = state) {
        VetsUiState.Loading ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

        is VetsUiState.Error ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Błąd: ${s.message}") }

        is VetsUiState.Success -> {
            if (s.vets.isEmpty()) {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak weterynarzy w tym gabinecie")
                }
                return
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxSize(),
            ) {
                items(s.vets, key = { it.userId }) { vet ->
                    VetCard(vet = vet, onClick = { onVetClick(vet.userId) })
                }
            }
        }
    }
}

@Composable
private fun VetCard(
    vet: Vet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
            Text("${vet.firstName} ${vet.lastName}", style = MaterialTheme.typography.titleMedium)
            vet.specialization?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}