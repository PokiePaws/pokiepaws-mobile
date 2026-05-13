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
import com.pokiepaws.mobile.domain.model.Clinic

@Composable
fun ClinicsScreen(
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClinicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Box(modifier = modifier.fillMaxSize()) {
        ClinicsContent(
            state = state,
            onClinicClick = onClinicClick,
        )
    }
}

@Composable
private fun ClinicsContent(
    state: ClinicsUiState,
    onClinicClick: (Long) -> Unit,
) {
    when (state) {
        ClinicsUiState.Loading -> LoadingView()
        is ClinicsUiState.Error -> MessageView(text = "Blad: ${state.message}")
        is ClinicsUiState.Success -> ClinicsResult(state = state, onClinicClick = onClinicClick)
    }
}

@Composable
private fun ClinicsResult(
    state: ClinicsUiState.Success,
    onClinicClick: (Long) -> Unit,
) {
    if (state.clinics.isEmpty()) {
        MessageView(text = "Brak gabinetow")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.clinics, key = { it.id }) { clinic ->
                ClinicCard(
                    clinic = clinic,
                    onClick = { onClinicClick(clinic.id) },
                )
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
private fun ClinicCard(
    clinic: Clinic,
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
            Text(clinic.clinicName, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "${clinic.street} ${clinic.houseNumber}, ${clinic.postalCode} ${clinic.city}",
                style = MaterialTheme.typography.bodySmall,
            )
            clinic.phone?.let {
                Spacer(Modifier.height(4.dp))
                Text("Phone: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
