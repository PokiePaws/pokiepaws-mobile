package com.pokiepaws.mobile.ui.visits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.domain.model.Visit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDetailScreen(
    visitId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VisitDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(visitId) { viewModel.load(visitId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły wizyty") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val s = state) {
                is VisitDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                is VisitDetailUiState.Error -> Text(
                    text = "Błąd: ${s.message}",
                    modifier = Modifier.align(Alignment.Center),
                )

                is VisitDetailUiState.Success -> VisitDetailContent(
                    visit = s.visit,
                    onCancelClick = { showCancelDialog = true },
                )
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Anuluj wizytę") },
            text = { Text("Czy na pewno chcesz anulować tę wizytę?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancel(visitId, onDone = onBack)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Anuluj wizytę")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Zamknij")
                }
            },
        )
    }
}

@Composable
private fun VisitDetailContent(
    visit: Visit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // --- Podstawowe informacje ---
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Wizyta #${visit.id}",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(label = "Status", value = visit.status)
                DetailRow(label = "Start", value = visit.startsAt)
                DetailRow(label = "Koniec", value = visit.endsAt)
                if (!visit.description.isNullOrBlank()) {
                    DetailRow(label = "Opis", value = visit.description)
                }
            }
        }

        // --- Dane medyczne (PP-14) — widoczne po wizycie ---
        val hasMedicalData = !visit.disease.isNullOrBlank()
                || !visit.diagnosis.isNullOrBlank()
                || !visit.recommendations.isNullOrBlank()

        if (hasMedicalData) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dane medyczne",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!visit.disease.isNullOrBlank()) {
                        DetailRow(label = "Choroba", value = visit.disease)
                    }
                    if (!visit.diagnosis.isNullOrBlank()) {
                        DetailSection(label = "Diagnoza", value = visit.diagnosis)
                    }
                    if (!visit.recommendations.isNullOrBlank()) {
                        DetailSection(label = "Zalecenia", value = visit.recommendations)
                    }
                }
            }
        }

        if (visit.status == "SCHEDULED") {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("Anuluj wizytę")
            }
        }
    }
}

// DetailRow dla krótkich wartości (jedna linia)
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

// DetailSection dla długich tekstów (diagnoza, zalecenia)
@Composable
private fun DetailSection(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}