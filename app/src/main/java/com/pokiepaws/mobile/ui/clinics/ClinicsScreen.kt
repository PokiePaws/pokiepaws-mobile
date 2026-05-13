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
import com.pokiepaws.mobile.domain.model.Clinic

@Composable
fun ClinicsScreen(
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClinicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    when (val s = state) {
        ClinicsUiState.Loading ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

        is ClinicsUiState.Error ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Błąd: ${s.message}") }

        is ClinicsUiState.Success -> {
            if (s.clinics.isEmpty()) {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak gabinetów")
                }
                return
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxSize(),
            ) {
                items(s.clinics, key = { it.id }) { clinic ->
                    ClinicCard(
                        clinic = clinic,
                        onClick = { onClinicClick(clinic.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ClinicCard(
    clinic: Clinic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
            Text(clinic.clinicName, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "${clinic.street} ${clinic.houseNumber}, ${clinic.postalCode} ${clinic.city}",
                style = MaterialTheme.typography.bodySmall,
            )
            clinic.phone?.let {
                Spacer(Modifier.height(4.dp))
                Text("📞 $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}